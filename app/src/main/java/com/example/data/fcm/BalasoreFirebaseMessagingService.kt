package com.example.data.fcm

import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.NewsArticleEntity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Firebase Cloud Messaging Service for handling real-time push notifications
 * regarding urgent weather alerts (cyclones, tidal swells, Chandipur warnings)
 * and breaking civic/news updates in the Balasore area.
 */
class BalasoreFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New Firebase Cloud Messaging registration token: $token")
        FcmManager.saveToken(applicationContext, token)

        // Resubscribe to default topics
        FcmManager.subscribeToTopic(FcmManager.TOPIC_WEATHER_ALERTS)
        FcmManager.subscribeToTopic(FcmManager.TOPIC_BREAKING_NEWS)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Received FCM message from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        val type = data["type"] ?: "GENERAL"
        val title = data["title"] ?: notification?.title ?: "Balasore Live Alert"
        val body = data["body"] ?: data["message"] ?: notification?.body ?: "New urgent update received for Balasore."
        val alertLevel = data["alert_level"] ?: "URGENT"
        val articleId = data["article_id"]

        when {
            type.contains("WEATHER", ignoreCase = true) ||
                    title.contains("Weather", ignoreCase = true) ||
                    title.contains("Cyclone", ignoreCase = true) ||
                    title.contains("Tide", ignoreCase = true) -> {
                // Update local Room database with latest weather alert
                updateRoomWeatherCache(title, body, alertLevel)

                // Dispatch high-priority system notification
                BalasoreNotificationHelper.showWeatherAlertNotification(
                    context = applicationContext,
                    title = title,
                    message = body,
                    alertLevel = alertLevel
                )
            }

            type.contains("NEWS", ignoreCase = true) ||
                    type.contains("BREAKING", ignoreCase = true) ||
                    title.contains("Breaking", ignoreCase = true) -> {
                // Insert into Room database
                insertBreakingNewsToRoom(title, body, data)

                // Dispatch system notification
                BalasoreNotificationHelper.showBreakingNewsNotification(
                    context = applicationContext,
                    title = title,
                    message = body,
                    articleId = articleId
                )
            }

            else -> {
                // Default to breaking news notification
                insertBreakingNewsToRoom(title, body, data)
                BalasoreNotificationHelper.showBreakingNewsNotification(
                    context = applicationContext,
                    title = title,
                    message = body,
                    articleId = articleId
                )
            }
        }
    }

    private fun updateRoomWeatherCache(title: String, message: String, level: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(applicationContext)
                val current = db.weatherDao().getWeatherCacheSync()
                if (current != null) {
                    val updated = current.copy(
                        alertLevel = if (level.isNotBlank()) level else "WARNING",
                        alertTitle = title,
                        alertMessage = message,
                        lastUpdated = System.currentTimeMillis()
                    )
                    db.weatherDao().insertOrUpdateWeather(updated)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating Room weather cache from FCM", e)
            }
        }
    }

    private fun insertBreakingNewsToRoom(title: String, body: String, data: Map<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(applicationContext)
                val timeString = SimpleDateFormat("h:mm a, d MMM", Locale.getDefault()).format(Date())
                val newArticle = NewsArticleEntity(
                    title = title,
                    summary = body,
                    content = data["content"] ?: body,
                    category = data["category"] ?: "Civic",
                    source = data["source"] ?: "Balasore Live Wire",
                    publishedAt = "Just now ($timeString)",
                    timestamp = System.currentTimeMillis(),
                    isBreaking = true,
                    isBookmarked = false
                )
                db.newsDao().insertArticles(listOf(newArticle))
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting breaking news into Room from FCM", e)
            }
        }
    }

    companion object {
        private const val TAG = "BalasoreFcmService"
    }
}
