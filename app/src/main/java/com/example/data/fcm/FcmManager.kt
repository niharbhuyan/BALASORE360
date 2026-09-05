package com.example.data.fcm

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.NewsArticleEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FcmManager {

    private const val TAG = "FcmManager"
    private const val PREFS_NAME = "balasore_fcm_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"
    private const val KEY_WEATHER_ALERTS = "topic_weather_alerts"
    private const val KEY_BREAKING_NEWS = "topic_breaking_news"

    const val TOPIC_WEATHER_ALERTS = "balasore_weather_alerts"
    const val TOPIC_BREAKING_NEWS = "balasore_breaking_news"

    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    private val _weatherAlertsEnabled = MutableStateFlow(true)
    val weatherAlertsEnabled: StateFlow<Boolean> = _weatherAlertsEnabled.asStateFlow()

    private val _breakingNewsEnabled = MutableStateFlow(true)
    val breakingNewsEnabled: StateFlow<Boolean> = _breakingNewsEnabled.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Initializes Firebase and FCM topic subscriptions safely, handling environments
     * where google-services.json might be pre-configured or running in dev mode.
     */
    fun initialize(context: Context) {
        BalasoreNotificationHelper.createNotificationChannels(context)

        val prefs = getPrefs(context)
        _fcmToken.value = prefs.getString(KEY_FCM_TOKEN, null)
        _weatherAlertsEnabled.value = prefs.getBoolean(KEY_WEATHER_ALERTS, true)
        _breakingNewsEnabled.value = prefs.getBoolean(KEY_BREAKING_NEWS, true)

        try {
            ensureFirebaseInitialized(context)

            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Log.d(TAG, "FCM registration token obtained: $token")
                    saveToken(context, token)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to retrieve FCM registration token", e)
                }

            // Ensure topics are subscribed if enabled
            if (_weatherAlertsEnabled.value) {
                subscribeToTopic(TOPIC_WEATHER_ALERTS)
            }
            if (_breakingNewsEnabled.value) {
                subscribeToTopic(TOPIC_BREAKING_NEWS)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FCM", e)
        }
    }

    private fun ensureFirebaseInitialized(context: Context) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            // Provide a graceful fallback configuration if google-services.json was not loaded
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:613265325843:android:balasoreliveapp")
                .setProjectId("balasore-live")
                .setApiKey("AIzaSyFallbackKeyForBalasoreLiveApp2026")
                .build()
            FirebaseApp.initializeApp(context, options)
            Log.i(TAG, "Firebase initialized with fallback configuration")
        }
    }

    fun saveToken(context: Context, token: String) {
        _fcmToken.value = token
        getPrefs(context).edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    fun setWeatherAlertsEnabled(context: Context, enabled: Boolean) {
        _weatherAlertsEnabled.value = enabled
        getPrefs(context).edit().putBoolean(KEY_WEATHER_ALERTS, enabled).apply()
        if (enabled) {
            subscribeToTopic(TOPIC_WEATHER_ALERTS)
        } else {
            unsubscribeFromTopic(TOPIC_WEATHER_ALERTS)
        }
    }

    fun setBreakingNewsEnabled(context: Context, enabled: Boolean) {
        _breakingNewsEnabled.value = enabled
        getPrefs(context).edit().putBoolean(KEY_BREAKING_NEWS, enabled).apply()
        if (enabled) {
            subscribeToTopic(TOPIC_BREAKING_NEWS)
        } else {
            unsubscribeFromTopic(TOPIC_BREAKING_NEWS)
        }
    }

    fun subscribeToTopic(topic: String) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnSuccessListener {
                    Log.d(TAG, "Subscribed successfully to FCM topic: $topic")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to subscribe to FCM topic: $topic", e)
                }
        } catch (e: Exception) {
            Log.w(TAG, "Cannot subscribe to topic $topic: ${e.message}")
        }
    }

    fun unsubscribeFromTopic(topic: String) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnSuccessListener {
                    Log.d(TAG, "Unsubscribed from FCM topic: $topic")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to unsubscribe from topic: $topic", e)
                }
        } catch (e: Exception) {
            Log.w(TAG, "Cannot unsubscribe from topic $topic: ${e.message}")
        }
    }

    /**
     * Simulates an urgent real-time Weather Alert push notification.
     * Updates the local Room database and triggers an Android system notification.
     */
    fun simulateWeatherAlertPush(context: Context) {
        val title = "IMD Coastal Storm & Tide Advisory"
        val message = "High tidal swell waves up to 4.5m and squally coastal winds (45–55 km/h) forecasted for Chandipur Beach and Balaramgadi coast over the next 12 hours. Fishermen and beach tourists advised to avoid deep-water recession."

        // Update local Room database in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val current = db.weatherDao().getWeatherCacheSync()
                if (current != null) {
                    val updated = current.copy(
                        alertLevel = "WARNING",
                        alertTitle = title,
                        alertMessage = message,
                        lastUpdated = System.currentTimeMillis()
                    )
                    db.weatherDao().insertOrUpdateWeather(updated)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update Room weather cache on push simulation", e)
            }
        }

        // Post system notification
        BalasoreNotificationHelper.showWeatherAlertNotification(
            context = context,
            title = title,
            message = message,
            alertLevel = "URGENT"
        )
    }

    /**
     * Simulates a real-time Breaking News push notification.
     * Inserts the urgent article directly into Room and fires a system notification.
     */
    fun simulateBreakingNewsPush(context: Context) {
        val timeString = SimpleDateFormat("h:mm a, d MMM", Locale.getDefault()).format(Date())
        val title = "District Admin Issues Chandipur Shoreline Advisory"
        val summary = "District Administration Balasore has issued an emergency safety alert urging visitors to adhere to the low tide return timings at Chandipur beach."
        val content = "Following an advisory from the Coastal Safety Division and IMD, the District Magistrate and Collector of Balasore have notified all coastal checkpoints at Chandipur, Kasafal, and Talasari. Visitors walking onto the receded seabed during low tide are instructed to return to the high-water berm line before 4:30 PM. NDRF and ODRAF personnel have deployed mobile patrol quad bikes along the shoreline."

        // Insert into Room Database so it is immediately cached and works offline
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val newArticle = NewsArticleEntity(
                    title = title,
                    summary = summary,
                    content = content,
                    category = "Civic",
                    source = "Collectorate & District Magistrate, Balasore",
                    publishedAt = "Just now ($timeString)",
                    timestamp = System.currentTimeMillis(),
                    isBreaking = true,
                    isBookmarked = false
                )
                db.newsDao().insertArticles(listOf(newArticle))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert breaking news article into Room on push simulation", e)
            }
        }

        // Post system notification
        BalasoreNotificationHelper.showBreakingNewsNotification(
            context = context,
            title = title,
            message = summary
        )
    }
}
