package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.sync.SyncManager
import com.example.ui.theme.BentoAmberBg
import com.example.ui.theme.BentoAmberText
import com.example.ui.theme.BentoBlueLight
import com.example.ui.theme.BentoBluePill
import com.example.ui.theme.BentoBlueText
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCanvas
import com.example.ui.theme.BentoCardWhite
import com.example.ui.theme.BentoGreenBg
import com.example.ui.theme.BentoGreenText
import com.example.ui.theme.BentoPrimaryBlue
import com.example.ui.theme.BentoSlate400
import com.example.ui.theme.BentoSlate500
import com.example.ui.theme.BentoSlate700
import com.example.ui.theme.BentoSlate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineStatusBottomSheet(
    isOpen: Boolean,
    isOnline: Boolean,
    isSyncing: Boolean,
    lastSyncTime: Long,
    newsCount: Int,
    hotspotsCount: Int,
    hasWeatherCache: Boolean,
    onClose: () -> Unit,
    onSyncNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val rotation by rememberInfiniteTransition(label = "sync_rotate").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = BentoCanvas,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.testTag("offline_status_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isOnline) BentoGreenBg else BentoAmberBg,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = if (isOnline) BentoGreenText else BentoAmberText,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Offline & Sync Engine",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = BentoSlate900
                        )
                        Text(
                            text = "Powered by AndroidX WorkManager & Room",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoSlate500
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_offline_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = BentoSlate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Connectivity Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOnline) BentoBlueLight else BentoAmberBg.copy(alpha = 0.5f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (isOnline) BentoPrimaryBlue.copy(alpha = 0.3f) else BentoAmberText.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (isOnline) BentoGreenText else BentoAmberText,
                                        shape = CircleShape
                                    )
                            )
                            Text(
                                text = if (isOnline) "ONLINE • LIVE SYNC ACTIVE" else "OFFLINE MODE ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = if (isOnline) BentoPrimaryBlue else BentoAmberText
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isOnline) {
                                "Connected to network. Background sync periodically refreshes the local Room cache."
                            } else {
                                "No active network connection. The app is serving all news, weather tides, and tourism hotspots seamlessly from the Room database."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoSlate700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bento Grid of Room Cached Tables
            Text(
                text = "CACHED ROOM MODULES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BentoSlate500
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // News Cache Card
                CacheEntityCard(
                    icon = Icons.Default.Newspaper,
                    title = "News Wire",
                    badge = "$newsCount Articles",
                    description = "Local bulletins, civic feeds & bookmarks",
                    isReady = newsCount > 0,
                    modifier = Modifier.weight(1f)
                )

                // Weather Cache Card
                CacheEntityCard(
                    icon = Icons.Default.WbSunny,
                    title = "Weather & Tides",
                    badge = if (hasWeatherCache) "Cached" else "Pending",
                    description = "Chandipur tide tables & coastal alerts",
                    isReady = hasWeatherCache,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tourism Cache Card
                CacheEntityCard(
                    icon = Icons.Default.Explore,
                    title = "Tourism Directory",
                    badge = "$hotspotsCount Hotspots",
                    description = "Heritage, beaches & offline map coordinates",
                    isReady = hotspotsCount > 0,
                    modifier = Modifier.weight(1f)
                )

                // Worker Engine Card
                CacheEntityCard(
                    icon = Icons.Default.Storage,
                    title = "Sync Worker",
                    badge = "Every 15m",
                    description = "WorkManager Coroutine background tasks",
                    isReady = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sync Status & Action
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Last Cache Sync",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = BentoSlate900
                            )
                            Text(
                                text = SyncManager.formatSyncTime(lastSyncTime),
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoSlate500
                            )
                        }

                        Button(
                            onClick = onSyncNow,
                            enabled = !isSyncing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoPrimaryBlue,
                                disabledContainerColor = BentoBluePill
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("trigger_sync_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = if (isSyncing) BentoBlueText else Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .rotate(if (isSyncing) rotation else 0f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSyncing) "Syncing..." else "Sync Now",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSyncing) BentoBlueText else Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "The background sync worker runs automatically every 15 minutes when connected to WiFi or Mobile Data, caching the newest district updates to ensure uninterrupted offline browsing across Balasore.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = BentoSlate500
                    )
                }
            }
        }
    }
}

@Composable
private fun CacheEntityCard(
    icon: ImageVector,
    title: String,
    badge: String,
    description: String,
    isReady: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, BentoBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = BentoBlueLight,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = BentoPrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isReady) BentoGreenBg else BentoAmberBg
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = if (isReady) BentoGreenText else BentoAmberText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = BentoSlate900
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                color = BentoSlate500,
                maxLines = 2
            )
        }
    }
}
