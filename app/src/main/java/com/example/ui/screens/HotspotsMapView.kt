package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HotspotEntity
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCanvas
import com.example.ui.theme.BentoCardWhite
import com.example.ui.theme.BentoPrimaryBlue
import com.example.ui.theme.BentoSlate500
import com.example.ui.theme.BentoSlate700
import com.example.ui.theme.BentoSlate900
import kotlin.math.hypot

// Balasore district bounding box coordinates
private const val BALASORE_MIN_LAT = 21.32
private const val BALASORE_MAX_LAT = 21.72
private const val BALASORE_MIN_LNG = 86.58
private const val BALASORE_MAX_LNG = 87.58

// Center point: Balasore Town
private const val CENTER_LAT = 21.4934
private const val CENTER_LNG = 86.9325

@Composable
fun HotspotsMapView(
    hotspots: List<HotspotEntity>,
    selectedHotspot: HotspotEntity?,
    onSelectHotspot: (HotspotEntity?) -> Unit,
    onViewHotspotDetails: (HotspotEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }

    // Map Pan & Zoom states
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    // Filter hotspots
    val visibleHotspots = remember(hotspots, selectedCategory) {
        if (selectedCategory == "All") hotspots
        else hotspots.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    // Pulse animation for selected pin
    val infiniteTransition = rememberInfiniteTransition(label = "marker_pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    val categories = listOf("All", "Beach", "Temple", "Wildlife", "Heritage", "Port")

    Box(modifier = modifier.fillMaxSize().background(BentoCanvas)) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val widthPx = constraints.maxWidth.toFloat()
            val heightPx = constraints.maxHeight.toFloat()

            // Function to map GPS (lat, lng) to canvas pixels
            fun geoToPixel(lat: Double, lng: Double): Offset {
                val normalizedX = (lng - BALASORE_MIN_LNG) / (BALASORE_MAX_LNG - BALASORE_MIN_LNG)
                val normalizedY = 1.0 - ((lat - BALASORE_MIN_LAT) / (BALASORE_MAX_LAT - BALASORE_MIN_LAT))

                val baseX = (normalizedX * widthPx).toFloat()
                val baseY = (normalizedY * heightPx).toFloat()

                // Center-based zoom transform
                val centerX = widthPx / 2f
                val centerY = heightPx / 2f

                val transformedX = centerX + (baseX - centerX + panOffsetX) * zoomScale
                val transformedY = centerY + (baseY - centerY + panOffsetY) * zoomScale
                return Offset(transformedX, transformedY)
            }

            // Interactive Map Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            zoomScale = (zoomScale * zoom).coerceIn(0.7f, 3.5f)
                            panOffsetX += pan.x / zoomScale
                            panOffsetY += pan.y / zoomScale
                        }
                    }
                    .pointerInput(visibleHotspots, zoomScale, panOffsetX, panOffsetY) {
                        detectTapGestures { tapOffset ->
                            // Check if tapped near any hotspot marker
                            var foundHotspot: HotspotEntity? = null
                            for (hotspot in visibleHotspots) {
                                val markerPos = geoToPixel(hotspot.latitude, hotspot.longitude)
                                val distance = hypot(tapOffset.x - markerPos.x, tapOffset.y - markerPos.y)
                                if (distance <= 45f * zoomScale.coerceAtLeast(0.9f)) {
                                    foundHotspot = hotspot
                                    break
                                }
                            }
                            onSelectHotspot(foundHotspot)
                        }
                    }
                    .testTag("interactive_map_canvas")
            ) {
                // 1. Draw Land and District Contours
                drawDistrictLandscape(
                    width = size.width,
                    height = size.height,
                    zoom = zoomScale,
                    panX = panOffsetX,
                    panY = panOffsetY
                )

                // 2. Draw Transport Corridors (NH-16 & Railways)
                drawHighwaysAndTransit(::geoToPixel, zoomScale)

                // 3. Draw Hotspot Markers
                for (hotspot in visibleHotspots) {
                    val pos = geoToPixel(hotspot.latitude, hotspot.longitude)
                    val isSelected = hotspot.id == selectedHotspot?.id

                    // Marker Pulse if selected
                    if (isSelected) {
                        drawCircle(
                            color = BentoPrimaryBlue.copy(alpha = pulseAlpha),
                            radius = pulseRadius * zoomScale.coerceAtLeast(0.8f),
                            center = pos
                        )
                    }

                    // Marker Outer Ring
                    val markerColor = getCategoryColor(hotspot.category)
                    drawCircle(
                        color = Color.White,
                        radius = (if (isSelected) 18f else 14f) * zoomScale.coerceAtLeast(0.8f),
                        center = pos
                    )
                    drawCircle(
                        color = markerColor,
                        radius = (if (isSelected) 15f else 11f) * zoomScale.coerceAtLeast(0.8f),
                        center = pos
                    )

                    // Small center dot
                    drawCircle(
                        color = Color.White,
                        radius = (if (isSelected) 6f else 4f) * zoomScale.coerceAtLeast(0.8f),
                        center = pos
                    )
                }
            }

            // Category Filter Chips (Top Bar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState())
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = when (category) {
                                    "Beach" -> "🏖️ Beaches"
                                    "Temple" -> "🛕 Temples"
                                    "Wildlife" -> "🐘 Wildlife"
                                    "Heritage" -> "🚀 Heritage"
                                    "Port" -> "⚓ Ports"
                                    else -> "All Spots (${hotspots.size})"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BentoPrimaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = BentoCardWhite
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = BentoBorder,
                            selectedBorderColor = BentoPrimaryBlue,
                            enabled = true,
                            selected = selectedCategory == category
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            // Zoom & Location Controls (Right Side)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Reset to Balasore Center
                FloatingActionButton(
                    onClick = {
                        zoomScale = 1.0f
                        panOffsetX = 0f
                        panOffsetY = 0f
                        Toast.makeText(context, "Centered on Balasore Town", Toast.LENGTH_SHORT).show()
                    },
                    shape = CircleShape,
                    containerColor = BentoCardWhite,
                    contentColor = BentoPrimaryBlue,
                    modifier = Modifier.size(44.dp).testTag("map_center_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Center on Balasore",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Zoom In
                FloatingActionButton(
                    onClick = { zoomScale = (zoomScale * 1.3f).coerceAtMost(3.5f) },
                    shape = CircleShape,
                    containerColor = BentoCardWhite,
                    contentColor = BentoSlate900,
                    modifier = Modifier.size(44.dp).testTag("map_zoom_in")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In")
                }

                // Zoom Out
                FloatingActionButton(
                    onClick = { zoomScale = (zoomScale / 1.3f).coerceAtLeast(0.7f) },
                    shape = CircleShape,
                    containerColor = BentoCardWhite,
                    contentColor = BentoSlate900,
                    modifier = Modifier.size(44.dp).testTag("map_zoom_out")
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out")
                }
            }

            // Legend / Scale info banner (Bottom Left)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BentoCardWhite.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = if (selectedHotspot != null) 170.dp else 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        tint = BentoPrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Balasore District • Bay of Bengal",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = BentoSlate700
                    )
                }
            }

            // Selected Hotspot Preview Bento Card (Bottom Sheet Card)
            AnimatedVisibility(
                visible = selectedHotspot != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                if (selectedHotspot != null) {
                    HotspotMapPreviewCard(
                        hotspot = selectedHotspot,
                        onClose = { onSelectHotspot(null) },
                        onGetDirections = {
                            launchNavigationIntent(context, selectedHotspot)
                        },
                        onViewDetails = { onViewHotspotDetails(selectedHotspot) }
                    )
                }
            }
        }
    }
}

@Composable
fun HotspotMapPreviewCard(
    hotspot: HotspotEntity,
    onClose: () -> Unit,
    onGetDirections: () -> Unit,
    onViewDetails: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = BentoCardWhite,
        border = BorderStroke(1.5.dp, BentoBorder),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hotspot_map_preview_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Category Badge, Distance & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = getCategoryColor(hotspot.category).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${getCategoryEmoji(hotspot.category)} ${hotspot.category}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = getCategoryColor(hotspot.category),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${hotspot.distanceKmFromBls} km from Balasore",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoSlate500
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = BentoSlate500,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title and Odia Name
            Text(
                text = hotspot.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = BentoSlate900,
                maxLines = 1
            )
            Text(
                text = hotspot.odiaName,
                style = MaterialTheme.typography.bodySmall,
                color = BentoSlate500
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = hotspot.shortDescription,
                style = MaterialTheme.typography.bodySmall,
                color = BentoSlate700,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Get Directions & Full Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onGetDirections,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("get_directions_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Directions", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BentoPrimaryBlue),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("view_details_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = BentoPrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Details", color = BentoPrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Function to open Google Maps or installed navigation app
private fun launchNavigationIntent(context: Context, hotspot: HotspotEntity) {
    try {
        val geoUri = Uri.parse("geo:${hotspot.latitude},${hotspot.longitude}?q=${hotspot.latitude},${hotspot.longitude}(${Uri.encode(hotspot.name)})")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        val chooser = Intent.createChooser(mapIntent, "Open Navigation to ${hotspot.name}")
        context.startActivity(chooser)
    } catch (e: Exception) {
        // Fallback to Google Maps Web URL
        try {
            val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${hotspot.latitude},${hotspot.longitude}")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            context.startActivity(webIntent)
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to launch map navigation", Toast.LENGTH_SHORT).show()
        }
    }
}

// Custom Cartographic Canvas Drawing Helpers
private fun DrawScope.drawDistrictLandscape(
    width: Float,
    height: Float,
    zoom: Float,
    panX: Float,
    panY: Float
) {
    // 1. Base District Land
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFEFF5FB), Color(0xFFE5EEF8))
        ),
        size = size
    )

    // 2. Bay of Bengal Ocean Area (Eastern/Southeastern Coast)
    val seaPath = Path().apply {
        moveTo(width * 0.58f, 0f)
        cubicTo(
            width * 0.62f, height * 0.25f,
            width * 0.55f, height * 0.55f,
            width * 0.45f, height * 0.85f
        )
        lineTo(width * 0.40f, height)
        lineTo(width, height)
        lineTo(width, 0f)
        close()
    }
    drawPath(
        path = seaPath,
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF3B82F6).copy(alpha = 0.45f), Color(0xFF1D4ED8).copy(alpha = 0.65f))
        )
    )

    // Bay of Bengal gentle wave contours
    val coastLinePath = Path().apply {
        moveTo(width * 0.58f, 0f)
        cubicTo(
            width * 0.62f, height * 0.25f,
            width * 0.55f, height * 0.55f,
            width * 0.45f, height * 0.85f
        )
        lineTo(width * 0.40f, height)
    }
    drawPath(
        path = coastLinePath,
        color = Color(0xFF60A5FA),
        style = Stroke(width = 3.5f * zoom.coerceAtLeast(0.8f))
    )

    // 3. Nilagiri Hills and Kuldiha Wildlife Forest Reserve (West & South-West)
    val forestHillsPath = Path().apply {
        moveTo(0f, height * 0.35f)
        cubicTo(
            width * 0.20f, height * 0.40f,
            width * 0.28f, height * 0.65f,
            width * 0.22f, height
        )
        lineTo(0f, height)
        close()
    }
    drawPath(
        path = forestHillsPath,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF10B981).copy(alpha = 0.25f), Color(0xFF059669).copy(alpha = 0.12f)),
            center = Offset(width * 0.1f, height * 0.6f),
            radius = width * 0.4f
        )
    )

    // 4. Budhabalanga River Path (cutting from NW into Balaramgadi Port)
    val budhabalangaPath = Path().apply {
        moveTo(width * 0.18f, height * 0.30f)
        cubicTo(
            width * 0.32f, height * 0.38f,
            width * 0.40f, height * 0.48f,
            width * 0.55f, height * 0.56f
        )
    }
    drawPath(
        path = budhabalangaPath,
        color = Color(0xFF60A5FA),
        style = Stroke(width = 2.5f * zoom.coerceAtLeast(0.8f), cap = StrokeCap.Round)
    )

    // 5. Subarnarekha River Estuary (Northeast near Talasari)
    val subarnarekhaPath = Path().apply {
        moveTo(width * 0.45f, 0f)
        cubicTo(
            width * 0.55f, height * 0.08f,
            width * 0.70f, height * 0.12f,
            width * 0.82f, height * 0.18f
        )
    }
    drawPath(
        path = subarnarekhaPath,
        color = Color(0xFF60A5FA),
        style = Stroke(width = 3.0f * zoom.coerceAtLeast(0.8f), cap = StrokeCap.Round)
    )
}

// Highway & Rail Network
private fun DrawScope.drawHighwaysAndTransit(
    geoToPixel: (Double, Double) -> Offset,
    zoom: Float
) {
    // NH-16 Golden Quadrilateral corridor passing through Balasore
    val northPt = geoToPixel(21.72, 87.12)
    val blsPt = geoToPixel(21.4934, 86.9325)
    val southPt = geoToPixel(21.32, 86.75)

    val highwayPath = Path().apply {
        moveTo(northPt.x, northPt.y)
        lineTo(blsPt.x, blsPt.y)
        lineTo(southPt.x, southPt.y)
    }
    drawPath(
        path = highwayPath,
        color = Color(0xFF94A3B8),
        style = Stroke(width = 3.0f * zoom.coerceAtLeast(0.8f), cap = StrokeCap.Round)
    )

    // Balasore Town Center Anchor Point
    drawCircle(
        color = Color(0xFF1E293B),
        radius = 5.0f * zoom.coerceAtLeast(0.8f),
        center = blsPt
    )
}

private fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "beach" -> Color(0xFF0284C7)
        "temple" -> Color(0xFFD97706)
        "wildlife" -> Color(0xFF16A34A)
        "heritage" -> Color(0xFF4F46E5)
        "port" -> Color(0xFF0D9488)
        else -> BentoPrimaryBlue
    }
}

private fun getCategoryEmoji(category: String): String {
    return when (category.lowercase()) {
        "beach" -> "🏖️"
        "temple" -> "🛕"
        "wildlife" -> "🐘"
        "heritage" -> "🚀"
        "port" -> "⚓"
        else -> "📍"
    }
}
