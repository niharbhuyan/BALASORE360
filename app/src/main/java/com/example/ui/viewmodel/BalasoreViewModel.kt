package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.data.local.HotspotEntity
import com.example.data.local.NewsArticleEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.UserEntity
import com.example.data.local.WeatherCacheEntity
import com.example.data.repository.BalasoreRepository
import com.example.data.sync.NetworkMonitor
import com.example.data.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    HOTSPOTS("Tourism"),
    NEWS("Local News"),
    WEATHER("Weather & Alerts"),
    ESSENTIALS("Directory & Info")
}

enum class AuthMode {
    PROFILE,
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD,
    EDIT_PROFILE
}

data class UiState(
    val isRefreshing: Boolean = false,
    val isSyncing: Boolean = false,
    val isOnline: Boolean = true,
    val lastSyncTime: Long = 0L,
    val isOfflineSheetOpen: Boolean = false,
    val selectedTab: AppTab = AppTab.HOTSPOTS,
    val newsCategory: String = "All",
    val hotspotCategory: String = "All",
    val newsSearchQuery: String = "",
    val selectedHotspot: HotspotEntity? = null,
    val selectedArticle: NewsArticleEntity? = null,
    val userNotice: String? = null,
    val isMapMode: Boolean = false,
    val selectedMapHotspot: HotspotEntity? = null,
    val isAuthSheetOpen: Boolean = false,
    val authMode: AuthMode = AuthMode.PROFILE,
    val authError: String? = null,
    val authSuccessMessage: String? = null
)

class BalasoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BalasoreRepository.getInstance(application)
    private val networkMonitor = NetworkMonitor(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rawHotspots: StateFlow<List<HotspotEntity>> = repository.allHotspots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawNews: StateFlow<List<NewsArticleEntity>> = repository.allNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weatherState: StateFlow<WeatherCacheEntity?> = repository.weatherCache
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allReviews: StateFlow<List<ReviewEntity>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Hotspots based on category
    val filteredHotspots: StateFlow<List<HotspotEntity>> = combine(rawHotspots, _uiState) { list, state ->
        if (state.hotspotCategory == "All") {
            list
        } else if (state.hotspotCategory == "Favorites") {
            list.filter { it.isFavorite }
        } else {
            list.filter { it.category.equals(state.hotspotCategory, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered News based on category and search query
    val filteredNews: StateFlow<List<NewsArticleEntity>> = combine(rawNews, _uiState) { list, state ->
        list.filter { article ->
            val matchesCategory = state.newsCategory == "All" ||
                    (state.newsCategory == "Saved" && article.isBookmarked) ||
                    article.category.equals(state.newsCategory, ignoreCase = true)

            val matchesSearch = state.newsSearchQuery.isBlank() ||
                    article.title.contains(state.newsSearchQuery, ignoreCase = true) ||
                    article.summary.contains(state.newsSearchQuery, ignoreCase = true) ||
                    article.content.contains(state.newsSearchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeIfNeeded()
            val lastSync = repository.getLastSyncTimestamp()
            _uiState.value = _uiState.value.copy(lastSyncTime = lastSync)
            
            // Schedule periodic background sync using WorkManager (caches news, weather, tourism)
            SyncManager.schedulePeriodicSync(getApplication())
            
            // Trigger initial sync to ensure Room database is fresh
            refreshData(silent = true)
        }

        // Monitor Network State
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                val wasOffline = !_uiState.value.isOnline && online
                _uiState.value = _uiState.value.copy(isOnline = online)
                if (wasOffline) {
                    // When device comes back online, immediately run background sync
                    SyncManager.triggerImmediateSync(getApplication())
                    refreshData(silent = true)
                }
            }
        }

        // Observe WorkManager Background Sync Status
        viewModelScope.launch {
            SyncManager.observeImmediateSync(getApplication()).collect { workInfo ->
                if (workInfo != null) {
                    val isRunning = workInfo.state == WorkInfo.State.RUNNING || workInfo.state == WorkInfo.State.ENQUEUED
                    val lastSync = repository.getLastSyncTimestamp()
                    _uiState.value = _uiState.value.copy(
                        isSyncing = isRunning,
                        lastSyncTime = if (lastSync > 0) lastSync else _uiState.value.lastSyncTime
                    )
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        _uiState.value = _uiState.value.copy(
                            isSyncing = false,
                            lastSyncTime = repository.getLastSyncTimestamp()
                        )
                    }
                }
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun setHotspotCategory(category: String) {
        _uiState.value = _uiState.value.copy(hotspotCategory = category)
    }

    fun setNewsCategory(category: String) {
        _uiState.value = _uiState.value.copy(newsCategory = category)
    }

    fun setNewsSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(newsSearchQuery = query)
    }

    fun selectHotspot(hotspot: HotspotEntity?) {
        _uiState.value = _uiState.value.copy(selectedHotspot = hotspot)
    }

    fun selectArticle(article: NewsArticleEntity?) {
        _uiState.value = _uiState.value.copy(selectedArticle = article)
    }

    fun dismissNotice() {
        _uiState.value = _uiState.value.copy(userNotice = null)
    }

    fun openOfflineSheet() {
        _uiState.value = _uiState.value.copy(isOfflineSheetOpen = true)
    }

    fun closeOfflineSheet() {
        _uiState.value = _uiState.value.copy(isOfflineSheetOpen = false)
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, isSyncing = true)
            SyncManager.triggerImmediateSync(getApplication())
            val result = repository.syncAllData(forceNetwork = true)
            _uiState.value = _uiState.value.copy(
                isRefreshing = false,
                isSyncing = false,
                lastSyncTime = result.timestamp,
                userNotice = result.message
            )
        }
    }

    fun toggleFavorite(hotspot: HotspotEntity) {
        viewModelScope.launch {
            repository.toggleFavoriteHotspot(hotspot.id, hotspot.isFavorite)
        }
    }

    fun toggleBookmark(article: NewsArticleEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(article.id, article.isBookmarked)
        }
    }

    fun refreshData(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _uiState.value = _uiState.value.copy(isRefreshing = true)
            }
            try {
                // Trigger background worker sync
                SyncManager.triggerImmediateSync(getApplication())
                val syncResult = repository.syncAllData()
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    lastSyncTime = syncResult.timestamp,
                    userNotice = if (!silent) syncResult.message else null
                )
            } catch (_: Exception) {
                if (!silent) {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        userNotice = "Offline mode: Showing cached Balasore data from Room."
                    )
                }
            } finally {
                if (!silent) {
                    _uiState.value = _uiState.value.copy(isRefreshing = false)
                }
            }
        }
    }

    // --- Interactive Map State Controls ---
    fun toggleMapMode(enabled: Boolean? = null) {
        val next = enabled ?: !_uiState.value.isMapMode
        _uiState.value = _uiState.value.copy(isMapMode = next)
    }

    fun selectMapHotspot(hotspot: HotspotEntity?) {
        _uiState.value = _uiState.value.copy(selectedMapHotspot = hotspot)
    }

    // --- Auth Sheet Controls ---
    fun openAuthSheet(mode: AuthMode = AuthMode.PROFILE) {
        _uiState.value = _uiState.value.copy(
            isAuthSheetOpen = true,
            authMode = mode,
            authError = null,
            authSuccessMessage = null
        )
    }

    fun setAuthMode(mode: AuthMode) {
        _uiState.value = _uiState.value.copy(
            authMode = mode,
            authError = null,
            authSuccessMessage = null
        )
    }

    fun closeAuthSheet() {
        _uiState.value = _uiState.value.copy(
            isAuthSheetOpen = false,
            authError = null,
            authSuccessMessage = null
        )
    }

    fun clearAuthMessages() {
        _uiState.value = _uiState.value.copy(authError = null, authSuccessMessage = null)
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authError = null, authSuccessMessage = null)
            val result = repository.login(email, pass)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    authMode = AuthMode.PROFILE,
                    authSuccessMessage = "Welcome back, ${user.fullName}!"
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(authError = err.message ?: "Login failed")
            }
        }
    }

    fun signUp(
        name: String,
        email: String,
        pass: String,
        phone: String,
        locality: String,
        securityAnswer: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authError = null, authSuccessMessage = null)
            val result = repository.signUp(name, email, pass, phone, locality, securityAnswer)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    authMode = AuthMode.PROFILE,
                    authSuccessMessage = "Account created! Welcome, ${user.fullName}."
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(authError = err.message ?: "Sign up failed")
            }
        }
    }

    fun resetPassword(email: String, securityAnswer: String, newPass: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authError = null, authSuccessMessage = null)
            val result = repository.resetPassword(email, securityAnswer, newPass)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    authMode = AuthMode.LOGIN,
                    authSuccessMessage = "Password reset successfully! Please log in with your new password."
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(authError = err.message ?: "Password reset failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = _uiState.value.copy(
                authMode = AuthMode.LOGIN,
                authSuccessMessage = "You have logged out."
            )
        }
    }

    fun updateProfile(fullName: String, phone: String, locality: String, bio: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val updated = user.copy(
                fullName = fullName.ifBlank { user.fullName },
                phoneNumber = phone,
                locality = locality.ifBlank { user.locality },
                bio = bio
            )
            repository.updateUserProfile(updated)
            _uiState.value = _uiState.value.copy(authSuccessMessage = "Profile details updated successfully!")
        }
    }

    fun updateAvatar(avatarUri: String?) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.updateUserAvatar(user.id, avatarUri)
            _uiState.value = _uiState.value.copy(authSuccessMessage = "Profile picture updated!")
        }
    }

    // --- Feedback & Reviews ---
    fun getReviewsForTarget(targetType: String, targetId: String): Flow<List<ReviewEntity>> {
        return repository.getReviewsForTarget(targetType, targetId)
    }

    fun getAverageRating(targetType: String, targetId: String): Flow<Double?> {
        return repository.getAverageRating(targetType, targetId)
    }

    fun getReviewCount(targetType: String, targetId: String): Flow<Int> {
        return repository.getReviewCount(targetType, targetId)
    }

    fun submitReview(
        targetType: String,
        targetId: String,
        targetTitle: String,
        rating: Int,
        comment: String,
        guestName: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.submitReview(
                targetType = targetType,
                targetId = targetId,
                targetTitle = targetTitle,
                rating = rating,
                comment = comment,
                authorName = guestName
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    userNotice = "Thank you! Your rating and feedback were submitted."
                )
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    userNotice = err.message ?: "Failed to submit review"
                )
            }
        }
    }
}
