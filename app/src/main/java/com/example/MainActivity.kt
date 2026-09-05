package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.ContactPhone
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppHeader
import com.example.ui.screens.AuthBottomSheet
import com.example.ui.screens.EssentialsScreen
import com.example.ui.screens.NewsScreen
import com.example.ui.screens.OfflineStatusBottomSheet
import com.example.ui.screens.TourismScreen
import com.example.ui.screens.WeatherScreen
import com.example.ui.theme.BentoBluePill
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCanvas
import com.example.ui.theme.BentoCardWhite
import com.example.ui.theme.BentoPrimaryBlue
import com.example.ui.theme.BentoSlate400
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.BalasoreViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BalasoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BalasoreApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BalasoreApp(viewModel: BalasoreViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val weather by viewModel.weatherState.collectAsStateWithLifecycle()
    val hotspots by viewModel.filteredHotspots.collectAsStateWithLifecycle()
    val news by viewModel.filteredNews.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userNotice) {
        val notice = uiState.userNotice
        if (notice != null) {
            snackbarHostState.showSnackbar(notice)
            viewModel.dismissNotice()
        }
    }

    Scaffold(
        containerColor = BentoCanvas,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppHeader(
                weather = weather,
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refreshData() },
                currentUser = currentUser,
                onProfileClick = { viewModel.openAuthSheet() },
                isOnline = uiState.isOnline,
                isSyncing = uiState.isSyncing,
                lastSyncTime = uiState.lastSyncTime,
                onOfflineStatusClick = { viewModel.openOfflineSheet() }
            )
        },
        bottomBar = {
            Surface(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = BentoCardWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar")
            ) {
                NavigationBar(
                    containerColor = BentoCardWhite,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(72.dp)
                ) {
                    AppTab.values().forEach { tab ->
                        val isSelected = uiState.selectedTab == tab
                        val (iconFilled, iconOutlined) = when (tab) {
                            AppTab.HOTSPOTS -> Pair(Icons.Default.Explore, Icons.Outlined.Explore)
                            AppTab.NEWS -> Pair(Icons.Default.Article, Icons.Outlined.Article)
                            AppTab.WEATHER -> Pair(Icons.Default.Cloud, Icons.Outlined.Cloud)
                            AppTab.ESSENTIALS -> Pair(Icons.Default.ContactPhone, Icons.Outlined.ContactPhone)
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) iconFilled else iconOutlined,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.6.sp
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoPrimaryBlue,
                                selectedTextColor = BentoPrimaryBlue,
                                indicatorColor = BentoBluePill,
                                unselectedIconColor = BentoSlate400,
                                unselectedTextColor = BentoSlate400
                            ),
                            modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                AppTab.HOTSPOTS -> {
                    TourismScreen(
                        hotspots = hotspots,
                        weather = weather,
                        selectedCategory = uiState.hotspotCategory,
                        selectedHotspot = uiState.selectedHotspot,
                        isMapMode = uiState.isMapMode,
                        currentUser = currentUser,
                        onToggleMapMode = { viewModel.toggleMapMode() },
                        onOpenAuth = { viewModel.openAuthSheet() },
                        getReviewsForHotspot = { id -> viewModel.getReviewsForTarget("HOTSPOT", id) },
                        onSubmitHotspotReview = { id, name, rating, comment, guestName ->
                            viewModel.submitReview("HOTSPOT", id, name, rating, comment, guestName)
                        },
                        onCategorySelect = { viewModel.setHotspotCategory(it) },
                        onHotspotSelect = { viewModel.selectHotspot(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                    )
                }
                AppTab.NEWS -> {
                    NewsScreen(
                        articles = news,
                        selectedCategory = uiState.newsCategory,
                        searchQuery = uiState.newsSearchQuery,
                        selectedArticle = uiState.selectedArticle,
                        currentUser = currentUser,
                        onOpenAuth = { viewModel.openAuthSheet() },
                        getReviewsForArticle = { id -> viewModel.getReviewsForTarget("NEWS", id) },
                        onSubmitArticleReview = { id, title, rating, comment, guestName ->
                            viewModel.submitReview("NEWS", id, title, rating, comment, guestName)
                        },
                        onCategorySelect = { viewModel.setNewsCategory(it) },
                        onSearchQueryChange = { viewModel.setNewsSearchQuery(it) },
                        onArticleSelect = { viewModel.selectArticle(it) },
                        onToggleBookmark = { viewModel.toggleBookmark(it) }
                    )
                }
                AppTab.WEATHER -> {
                    WeatherScreen(
                        weather = weather,
                        currentUser = currentUser,
                        onOpenAuth = { viewModel.openAuthSheet() },
                        getReviewsForWeather = { id -> viewModel.getReviewsForTarget("WEATHER", id) },
                        onSubmitWeatherReview = { id, title, rating, comment, guestName ->
                            viewModel.submitReview("WEATHER", id, title, rating, comment, guestName)
                        }
                    )
                }
                AppTab.ESSENTIALS -> {
                    EssentialsScreen()
                }
            }
        }
    }

    // Full User Authentication, Profile Management & Avatar Upload Bottom Sheet
    AuthBottomSheet(
        isOpen = uiState.isAuthSheetOpen,
        authMode = uiState.authMode,
        currentUser = currentUser,
        errorMessage = uiState.authError,
        successMessage = uiState.authSuccessMessage,
        onClose = { viewModel.closeAuthSheet() },
        onSetMode = { viewModel.setAuthMode(it) },
        onLogin = { email, pass -> viewModel.login(email, pass) },
        onSignUp = { name, email, pass, phone, locality, answer ->
            viewModel.signUp(name, email, pass, phone, locality, answer)
        },
        onResetPassword = { email, answer, newPass ->
            viewModel.resetPassword(email, answer, newPass)
        },
        onUpdateProfile = { name, phone, locality, bio ->
            viewModel.updateProfile(name, phone, locality, bio)
        },
        onUpdateAvatar = { avatarUri ->
            viewModel.updateAvatar(avatarUri)
        },
        onLogout = { viewModel.logout() }
    )

    // Offline & WorkManager Background Cache Sync Sheet
    OfflineStatusBottomSheet(
        isOpen = uiState.isOfflineSheetOpen,
        isOnline = uiState.isOnline,
        isSyncing = uiState.isSyncing,
        lastSyncTime = uiState.lastSyncTime,
        newsCount = news.size,
        hotspotsCount = hotspots.size,
        hasWeatherCache = weather != null,
        onClose = { viewModel.closeOfflineSheet() },
        onSyncNow = { viewModel.triggerManualSync() }
    )
}
