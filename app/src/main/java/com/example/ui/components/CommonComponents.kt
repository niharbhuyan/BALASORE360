package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.UserEntity
import com.example.data.local.WeatherCacheEntity
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
import com.example.ui.theme.BentoRedBg
import com.example.ui.theme.BentoRedText
import com.example.ui.theme.BentoSlate400
import com.example.ui.theme.BentoSlate500
import com.example.ui.theme.BentoSlate700
import com.example.ui.theme.BentoSlate900
import com.example.ui.theme.TideIncoming
import com.example.ui.theme.TideReceding

@Composable
fun AppHeader(
    weather: WeatherCacheEntity?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    currentUser: UserEntity? = null,
    onProfileClick: () -> Unit = {},
    isOnline: Boolean = true,
    isSyncing: Boolean = false,
    lastSyncTime: Long = 0L,
    onOfflineStatusClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rotation by rememberInfiniteTransition(label = "refresh").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "rotate"
    )

    Surface(
        color = BentoCanvas,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIVE FROM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontSize = 11.sp
                        ),
                        color = BentoPrimaryBlue
                    )
                    Text(
                        text = "BALASORE",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = BentoSlate900
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "ବାଲେଶ୍ୱର • Odisha",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoSlate500
                        )

                        // Room Sync / Offline Pill Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (!isOnline) BentoAmberBg else if (isSyncing) BentoBlueLight else BentoCardWhite,
                            border = BorderStroke(1.dp, if (!isOnline) BentoAmberText.copy(alpha = 0.3f) else BentoBorder),
                            shadowElevation = 0.5.dp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onOfflineStatusClick)
                                .testTag("sync_status_badge")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = if (!isOnline) BentoAmberText else if (isSyncing) BentoPrimaryBlue else BentoGreenText,
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = if (!isOnline) "Offline (Room)" else if (isSyncing) "Syncing..." else "Room Cached",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    ),
                                    color = if (!isOnline) BentoAmberText else if (isSyncing) BentoPrimaryBlue else BentoSlate700
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (weather != null) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = BentoCardWhite,
                            border = BorderStroke(1.dp, BentoBorder),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = "Weather",
                                    tint = BentoPrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${weather.temperature.toInt()}°C",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BentoSlate900
                                )
                            }
                        }
                    }

                    // Refresh Button (Triggers Sync Worker & Cache Refresh)
                    Surface(
                        shape = CircleShape,
                        color = BentoBluePill,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onRefresh)
                            .testTag("refresh_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Data",
                                tint = BentoBlueText,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(if (isRefreshing || isSyncing) rotation else 0f)
                            )
                        }
                    }

                    // User Profile Button
                    Surface(
                        shape = CircleShape,
                        color = BentoCardWhite,
                        border = BorderStroke(1.5.dp, if (currentUser != null) BentoPrimaryBlue else BentoBorder),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onProfileClick)
                            .testTag("user_profile_header_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (currentUser?.avatarUri != null) {
                                AsyncImage(
                                    model = currentUser.avatarUri,
                                    contentDescription = "Profile",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(42.dp)
                                )
                            } else if (currentUser != null) {
                                Text(
                                    text = currentUser.fullName.take(2).uppercase(),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BentoPrimaryBlue
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Account Login",
                                    tint = BentoSlate500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Offline Mode Banner Indicator
            if (!isOnline) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BentoAmberBg,
                    border = BorderStroke(1.dp, BentoAmberText.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onOfflineStatusClick)
                        .testTag("offline_notice_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = BentoAmberText,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Offline Mode • All news, weather tides & tourism hotspots running from Room database",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                            color = BentoAmberText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TideStatusBadge(
    tideState: String,
    tideDesc: String,
    modifier: Modifier = Modifier
) {
    val (badgeBg, textColor, label) = when (tideState) {
        "LOW_TIDE" -> Triple(BentoBluePill, BentoPrimaryBlue, "Chandipur: Vanishing Sea (Low Tide)")
        "RECEDING" -> Triple(BentoBluePill, BentoPrimaryBlue, "Chandipur: Sea Receding Outwards")
        "INCOMING" -> Triple(BentoRedBg, BentoRedText, "Chandipur: Incoming Tide (Return to Shore)")
        else -> Triple(BentoBlueLight, BentoPrimaryBlue, "Chandipur: High Tide Waves")
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = badgeBg,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) BentoPrimaryBlue else BentoCardWhite,
        contentColor = if (isSelected) Color.White else BentoSlate700,
        border = if (isSelected) null else BorderStroke(1.dp, BentoBorder),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
