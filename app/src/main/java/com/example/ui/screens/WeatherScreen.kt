package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ReviewEntity
import com.example.data.local.UserEntity
import com.example.data.local.WeatherCacheEntity
import com.example.ui.components.ReviewsSection
import com.example.ui.theme.AlertCyclone
import com.example.ui.theme.AlertNormal
import com.example.ui.theme.AlertWarning
import com.example.ui.theme.BentoBlueLight
import com.example.ui.theme.BentoBluePill
import com.example.ui.theme.BentoBlueText
import com.example.ui.theme.BentoBorder
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
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherScreen(
    weather: WeatherCacheEntity?,
    currentUser: UserEntity? = null,
    onOpenAuth: () -> Unit = {},
    getReviewsForWeather: (String) -> Flow<List<ReviewEntity>> = { kotlinx.coroutines.flow.emptyFlow() },
    onSubmitWeatherReview: (weatherId: String, alertTitle: String, rating: Int, comment: String, guestName: String?) -> Unit = { _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    if (weather == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading Balasore meteorological data...", style = MaterialTheme.typography.bodyMedium, color = BentoSlate500)
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Bento Weather Primary Tile
        item {
            BentoWeatherHeroCard(weather = weather)
        }

        // Coastal Alert Bento Card
        item {
            BentoCoastalAlertCard(weather = weather)
        }

        // Chandipur Vanishing Sea Tide Bento Card
        item {
            BentoChandipurTideCard(weather = weather)
        }

        // Atmospheric & Marine Parameters Grid
        item {
            BentoAtmosphericMetricsCard(weather = weather)
        }

        // Tourism Weather Advisory Card
        item {
            BentoTourismAdvisoryCard(weather = weather)
        }

        // Community Weather Reports & Alert Feedback
        item {
            ReviewsSection(
                targetType = "WEATHER",
                targetId = "balasore_weather_today",
                targetTitle = "Live Weather Alerts & Ground Reports",
                reviewsFlow = getReviewsForWeather("balasore_weather_today"),
                currentUser = currentUser,
                onOpenAuth = onOpenAuth,
                onSubmitReview = { rating, comment, guestName ->
                    onSubmitWeatherReview("balasore_weather_today", "Balasore Weather Alerts", rating, comment, guestName)
                }
            )
        }
    }
}

/**
 * Bento Primary Weather Tile: Vibrant Royal Blue with rounded-3xl (28.dp)
 */
@Composable
fun BentoWeatherHeroCard(
    weather: WeatherCacheEntity,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoPrimaryBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("current_weather_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Balasore City & Coast",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "North Bay of Bengal • 21.49°N 86.91°E",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "LIVE RADAR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${weather.temperature.toInt()}°",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = 58.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "C",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = weather.weatherDescription,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Feels like ${weather.apparentTemperature.toInt()}°C",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "H: ${weather.maxTemp.toInt()}°  L: ${weather.minTemp.toInt()}°",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val timeStr = SimpleDateFormat("h:mm a, dd MMM", Locale.getDefault()).format(Date(weather.lastUpdated))
            Text(
                text = "Last updated: $timeStr",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Coastal Alert Bento Card
 */
@Composable
fun BentoCoastalAlertCard(
    weather: WeatherCacheEntity,
    modifier: Modifier = Modifier
) {
    val isAlert = weather.alertLevel != "NORMAL"
    val badgeBg = if (isAlert) BentoRedBg else BentoGreenBg
    val borderCol = if (isAlert) BentoRedBg else BentoBorder
    val iconCol = if (isAlert) BentoRedText else BentoGreenText
    val iconSymbol = if (isAlert) "⚠️" else "✅"

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, borderCol),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("coastal_alert_card")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = badgeBg,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iconSymbol, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = weather.alertTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = BentoSlate900
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = weather.alertMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoSlate500
                )
            }
        }
    }
}

/**
 * Chandipur Tide Bento Card
 */
@Composable
fun BentoChandipurTideCard(
    weather: WeatherCacheEntity,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("chandipur_tide_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BentoBlueLight,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🌊", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Chandipur Vanishing Sea",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = BentoSlate900
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (weather.tideState == "LOW_TIDE") BentoBlueLight else BentoRedBg
                ) {
                    Text(
                        text = weather.tideState.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = if (weather.tideState == "LOW_TIDE") BentoPrimaryBlue else BentoRedText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = weather.tideDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = BentoSlate500
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BentoBlueLight,
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = BentoPrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (weather.tideState == "LOW_TIDE") {
                            "Safe Seabed Walking: Ideal window to stroll out 1-4 km towards vanishing horizon."
                        } else {
                            "Lifeguard Alert: Watch water line; return towards beach promenade."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = BentoSlate700
                    )
                }
            }
        }
    }
}

/**
 * Atmospheric & Marine Parameters Grid: 6 Bento Tiles
 */
@Composable
fun BentoAtmosphericMetricsCard(
    weather: WeatherCacheEntity,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Atmospheric & Marine Parameters",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = BentoSlate900
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BentoMetricItem(
                    icon = Icons.Default.Air,
                    label = "Wind Speed",
                    value = "${weather.windSpeed.toInt()} km/h",
                    sub = "Gusts: ${weather.windGusts.toInt()} km/h",
                    modifier = Modifier.weight(1f)
                )
                BentoMetricItem(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "${weather.humidity}%",
                    sub = "Coastal air",
                    modifier = Modifier.weight(1f)
                )
                BentoMetricItem(
                    icon = Icons.Default.WbSunny,
                    label = "UV Index",
                    value = "${weather.uvIndex.toInt()}",
                    sub = if (weather.uvIndex > 7) "Very High" else "Moderate",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BentoMetricItem(
                    icon = Icons.Default.WbSunny,
                    label = "Sunrise",
                    value = weather.sunrise,
                    sub = "Bay horizon",
                    modifier = Modifier.weight(1f)
                )
                BentoMetricItem(
                    icon = Icons.Default.WbTwilight,
                    label = "Sunset",
                    value = weather.sunset,
                    sub = "Golden hour",
                    modifier = Modifier.weight(1f)
                )
                BentoMetricItem(
                    icon = Icons.Default.Thermostat,
                    label = "Max / Min",
                    value = "${weather.maxTemp.toInt()}° / ${weather.minTemp.toInt()}°",
                    sub = "Today",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun BentoMetricItem(
    icon: ImageVector,
    label: String,
    value: String,
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
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BentoPrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = BentoSlate500)
            Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = BentoSlate900)
            Text(text = sub, style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = BentoSlate400)
        }
    }
}

@Composable
fun BentoTourismAdvisoryCard(
    weather: WeatherCacheEntity,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BentoBlueLight,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🏖️", fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Balasore Traveler Weather Guide",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = BentoSlate900
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            BentoTravelTip(
                spot = "Chandipur & Talasari Beach",
                tip = "Best visiting time: 6:00 AM - 9:30 AM and after 4:00 PM when solar heat subsides."
            )
            Spacer(modifier = Modifier.height(8.dp))
            BentoTravelTip(
                spot = "Panchalingeswar & Nilagiri Hills",
                tip = "Mountain stream has pleasant cool water. Wear sturdy non-slip footwear on forest steps."
            )
            Spacer(modifier = Modifier.height(8.dp))
            BentoTravelTip(
                spot = "Remuna & Emami Jagannath",
                tip = "Evening aarti hours (6:00 PM – 8:00 PM) offer beautiful illuminations and cooler temperatures."
            )
        }
    }
}

@Composable
fun BentoTravelTip(spot: String, tip: String) {
    Column {
        Text(text = spot, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = BentoPrimaryBlue)
        Text(text = tip, style = MaterialTheme.typography.bodySmall, color = BentoSlate500)
    }
}
