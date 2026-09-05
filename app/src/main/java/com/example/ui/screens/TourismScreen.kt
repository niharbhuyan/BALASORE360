package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewAgenda
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.HotspotEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.UserEntity
import com.example.data.local.WeatherCacheEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.ReviewsSection
import kotlinx.coroutines.flow.Flow
import com.example.ui.theme.BentoAmberBg
import com.example.ui.theme.BentoAmberText
import com.example.ui.theme.BentoBlueLight
import com.example.ui.theme.BentoBluePill
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardWhite
import com.example.ui.theme.BentoDarkTile
import com.example.ui.theme.BentoPrimaryBlue
import com.example.ui.theme.BentoRedBg
import com.example.ui.theme.BentoRedText
import com.example.ui.theme.BentoSlate400
import com.example.ui.theme.BentoSlate500
import com.example.ui.theme.BentoSlate700
import com.example.ui.theme.BentoSlate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourismScreen(
    hotspots: List<HotspotEntity>,
    weather: WeatherCacheEntity? = null,
    selectedCategory: String,
    selectedHotspot: HotspotEntity?,
    isMapMode: Boolean = false,
    currentUser: UserEntity? = null,
    onToggleMapMode: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    getReviewsForHotspot: (String) -> Flow<List<ReviewEntity>> = { kotlinx.coroutines.flow.emptyFlow() },
    onSubmitHotspotReview: (hotspotId: String, hotspotName: String, rating: Int, comment: String, guestName: String?) -> Unit = { _, _, _, _, _ -> },
    onCategorySelect: (String) -> Unit,
    onHotspotSelect: (HotspotEntity?) -> Unit,
    onToggleFavorite: (HotspotEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Beach", "Temple", "Wildlife", "Heritage", "Port", "Favorites")

    Box(modifier = modifier.fillMaxSize()) {
        if (isMapMode) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top switcher bar for Map Mode
                Surface(
                    color = BentoCardWhite,
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Interactive Hotspots Map",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = BentoSlate900
                            )
                            Text(
                                text = "Tap markers to explore & navigate",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoSlate500
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = BentoBlueLight,
                            border = BorderStroke(1.dp, BentoBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(onClick = onToggleMapMode)
                                .testTag("toggle_list_view_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ViewAgenda,
                                    contentDescription = "List View",
                                    tint = BentoPrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "List View",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = BentoPrimaryBlue
                                )
                            }
                        }
                    }
                }

                HotspotsMapView(
                    hotspots = hotspots,
                    selectedHotspot = selectedHotspot,
                    onSelectHotspot = onHotspotSelect,
                    onViewHotspotDetails = { onHotspotSelect(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Bento Grid Top Cluster (Weather Card + Alert + Petrol/Tide Cards)
                item {
                    BentoTopGrid(
                        weather = weather,
                        onAlertClick = {
                            val chandipur = hotspots.find { it.id == "chandipur_beach" }
                            if (chandipur != null) onHotspotSelect(chandipur)
                        }
                    )
                }

                // Hero Bento Tile (Dark #1A1C1E card with Chandipur Beach)
                item {
                    BentoTourismHeroTile(
                        onExplore = {
                            val chandipur = hotspots.find { it.id == "chandipur_beach" }
                            if (chandipur != null) onHotspotSelect(chandipur)
                        }
                    )
                }

                // Category Filter Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            CategoryChip(
                                name = cat,
                                isSelected = cat == selectedCategory,
                                onClick = { onCategorySelect(cat) }
                            )
                        }
                    }
                }

                // Section Title with Map Mode Switcher
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedCategory == "All") "Popular Destinations" else "$selectedCategory Hotspots",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoSlate900
                                )
                            )
                            Text(
                                text = "${hotspots.size} locations",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoSlate500
                            )
                        }

                        // Map View Switcher Button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = BentoBlueLight,
                            border = BorderStroke(1.dp, BentoBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(onClick = onToggleMapMode)
                                .testTag("hotspot_map_view_toggle")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = "Interactive Map",
                                    tint = BentoPrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Map View",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = BentoPrimaryBlue
                                )
                            }
                        }
                    }
                }

                // Hotspots Bento Cards List
                if (hotspots.isEmpty()) {
                    item {
                        BentoEmptyStateCard(message = "No hotspots found in this category.")
                    }
                } else {
                    items(hotspots, key = { it.id }) { hotspot ->
                        HotspotBentoCard(
                            hotspot = hotspot,
                            onClick = { onHotspotSelect(hotspot) },
                            onToggleFavorite = { onToggleFavorite(hotspot) }
                        )
                    }
                }
            }
        }

        // Detail Bottom Sheet
        if (selectedHotspot != null) {
            HotspotDetailSheet(
                hotspot = selectedHotspot,
                currentUser = currentUser,
                reviewsFlow = getReviewsForHotspot(selectedHotspot.id),
                onOpenAuth = onOpenAuth,
                onSubmitReview = { rating, comment, guestName ->
                    onSubmitHotspotReview(selectedHotspot.id, selectedHotspot.name, rating, comment, guestName)
                },
                onDismiss = { onHotspotSelect(null) },
                onToggleFavorite = { onToggleFavorite(selectedHotspot) }
            )
        }
    }
}

/**
 * Bento Grid Top Row: Asymmetric 2-column layout matching the design HTML
 * Left: Blue Weather Card
 * Right: Stack of Alert Card and Petrol/Tide Card
 */
@Composable
fun BentoTopGrid(
    weather: WeatherCacheEntity?,
    onAlertClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left Bento Tile: Blue Weather Card (row-span-2)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BentoPrimaryBlue),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .testTag("bento_weather_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "☀️",
                        fontSize = 28.sp
                    )
                    Text(
                        text = "WEATHER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Column {
                    Text(
                        text = if (weather != null) "${weather.temperature.toInt()}°" else "32°",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = 38.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = if (weather != null) "${weather.weatherDescription} • ${weather.humidity}% hum" else "Clear Skies • 14:00",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Right Column: Stack of 2 Bento mini cards
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Alert Mini Tile
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
                border = BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable(onClick = onAlertClick)
                    .testTag("bento_alert_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BentoRedBg,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("⚠️", fontSize = 16.sp)
                        }
                    }
                    Column {
                        Text(
                            text = "Alert",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = BentoSlate900
                        )
                        Text(
                            text = if (weather != null && weather.alertLevel != "NORMAL") "Coastal Alert" else "Tide Warning",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = BentoSlate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Petrol / District Indicator Mini Tile
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
                border = BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("bento_petrol_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BentoAmberBg,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("⛽", fontSize = 16.sp)
                        }
                    }
                    Column {
                        Text(
                            text = "Petrol",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = BentoSlate900
                        )
                        Text(
                            text = "₹103.4/L",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = BentoSlate500
                        )
                    }
                }
            }
        }
    }
}

/**
 * Hero Bento Tile (col-span-2 row-span-2 bg-[#1A1C1E] rounded-3xl)
 */
@Composable
fun BentoTourismHeroTile(
    onExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoDarkTile),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(210.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.img_chandipur_beach),
                contentDescription = "Chandipur Beach",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient from-black/80 to-transparent
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.2f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Right Glassmorphic Pill Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.15f),
                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.25f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
            ) {
                Text(
                    text = "TOURISM",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            // Bottom Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(18.dp)
            ) {
                Text(
                    text = "Chandipur Beach",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Text(
                    text = "The Vanishing Sea • 16 km from Balasore",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onExplore,
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "Explore Hub",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Hotspot Card in Bento Grid Styling: rounded-3xl, subtle border, white surface
 */
@Composable
fun HotspotBentoCard(
    hotspot: HotspotEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("hotspot_card_${hotspot.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hotspot.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = BentoSlate900
                    )
                    Text(
                        text = hotspot.odiaName,
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoPrimaryBlue
                    )
                }
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (hotspot.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (hotspot.isFavorite) BentoRedText else BentoSlate400
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = hotspot.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = BentoSlate500,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BentoBlueLight
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Distance",
                            tint = BentoPrimaryBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${hotspot.distanceKmFromBls} km from BLS Stn",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = BentoSlate700
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BentoBlueLight
                ) {
                    Text(
                        text = hotspot.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = BentoPrimaryBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HotspotDetailSheet(
    hotspot: HotspotEntity,
    currentUser: UserEntity? = null,
    reviewsFlow: Flow<List<ReviewEntity>> = kotlinx.coroutines.flow.emptyFlow(),
    onOpenAuth: () -> Unit = {},
    onSubmitReview: (rating: Int, comment: String, guestName: String?) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BentoCardWhite,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hotspot.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = BentoSlate900
                    )
                    Text(
                        text = hotspot.odiaName,
                        style = MaterialTheme.typography.titleMedium,
                        color = BentoPrimaryBlue
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = BentoSlate500)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Info Bento Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BentoInfoTag(icon = Icons.Default.LocationOn, label = "${hotspot.distanceKmFromBls} km", sub = "from BLS Stn", modifier = Modifier.weight(1f))
                BentoInfoTag(icon = Icons.Default.Payments, label = hotspot.entryFee, sub = "Entry", modifier = Modifier.weight(1f))
                BentoInfoTag(icon = Icons.Default.Star, label = hotspot.category, sub = "Type", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "About this Destination",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = BentoSlate900
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = hotspot.fullDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = BentoSlate700
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Highlights",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = BentoSlate900
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                hotspot.highlights.split(",").forEach { highlight ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BentoBlueLight
                    ) {
                        Text(
                            text = highlight.trim(),
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoPrimaryBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Time", tint = BentoPrimaryBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Timings: ${hotspot.timings}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = BentoSlate700
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Local Tip Bento Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BentoAmberBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Tip", tint = BentoAmberText, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Local Insider Tip",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BentoAmberText
                        )
                        Text(
                            text = hotspot.localTip,
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoSlate900
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Visit ${hotspot.name} in Balasore")
                            putExtra(Intent.EXTRA_TEXT, "Check out ${hotspot.name} in Balasore, Odisha!\n\n${hotspot.shortDescription}\n\nTip: ${hotspot.localTip}\n\n- Shared via Balasore Live App")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Hotspot"))
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = BentoSlate700, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", color = BentoSlate700)
                }

                Button(
                    onClick = onToggleFavorite,
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (hotspot.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (hotspot.isFavorite) "Saved" else "Save")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Get Directions Button
            Button(
                onClick = {
                    val uri = Uri.parse("geo:${hotspot.latitude},${hotspot.longitude}?q=${hotspot.latitude},${hotspot.longitude}(${Uri.encode(hotspot.name)})")
                    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(Intent.createChooser(mapIntent, "Navigate to ${hotspot.name}"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = BentoDarkTile),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hotspot_get_directions_button")
            ) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = "Directions",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Get Directions in Navigation App",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Community Feedback & Reviews Section
            ReviewsSection(
                targetType = "HOTSPOT",
                targetId = hotspot.id,
                targetTitle = hotspot.name,
                reviewsFlow = reviewsFlow,
                currentUser = currentUser,
                onOpenAuth = onOpenAuth,
                onSubmitReview = onSubmitReview
            )
        }
    }
}

@Composable
fun BentoInfoTag(
    icon: ImageVector,
    label: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BentoBlueLight,
        border = BorderStroke(1.dp, BentoBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BentoPrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = BentoSlate900, maxLines = 1)
            Text(text = sub, style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = BentoSlate500)
        }
    }
}

@Composable
fun BentoEmptyStateCard(message: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, BentoBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = BentoSlate400,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = BentoSlate500
            )
        }
    }
}
