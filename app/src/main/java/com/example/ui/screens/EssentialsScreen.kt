package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoBlueLight
import com.example.ui.theme.BentoBluePill
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardWhite
import com.example.ui.theme.BentoPrimaryBlue
import com.example.ui.theme.BentoSlate400
import com.example.ui.theme.BentoSlate500
import com.example.ui.theme.BentoSlate700
import com.example.ui.theme.BentoSlate900

data class EmergencyContact(
    val name: String,
    val odiaName: String,
    val phone: String,
    val role: String,
    val icon: ImageVector
)

@Composable
fun EssentialsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val contacts = listOf(
        EmergencyContact("District Emergency Control Room", "ଜିଲ୍ଲା ଜରୁରୀକାଳୀନ କକ୍ଷ", "06782-262274", "Collectorate Balasore", Icons.Default.Security),
        EmergencyContact("Superintendent of Police (SP) Office", "ଆରକ୍ଷୀ ଅଧୀକ୍ଷକ କାର୍ଯ୍ୟାଳୟ", "06782-262024", "Police Control Room", Icons.Default.LocalPolice),
        EmergencyContact("FM Medical College & Hospital", "ଫକୀର ମୋହନ ମେଡିକାଲ କଲେଜ", "06782-255010", "Casualty & Emergency", Icons.Default.LocalHospital),
        EmergencyContact("Chandipur Marine Police Station", "ଚାନ୍ଦିପୁର ସାମୁଦ୍ରିକ ଥାନା", "06782-272100", "Coastal Security & Lifeguards", Icons.Default.LocalPolice),
        EmergencyContact("District Fire & ODRAF Rescue", "ଅଗ୍ନିଶମ ଓ ବିପର୍ଯ୍ୟୟ ପ୍ରଶମନ", "112", "Emergency Services", Icons.Default.Call),
        EmergencyContact("Tourist Information Counter (OTDC)", "ପର୍ଯ୍ୟଟନ ସୂଚନା କେନ୍ଦ୍ର", "06782-262048", "Balasore Railway Station", Icons.Default.Info)
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Emergency Section Header
        item {
            Text(
                text = "Emergency & District Helplines",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = BentoSlate900
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Tap any number to dial directly from your device.",
                style = MaterialTheme.typography.bodySmall,
                color = BentoSlate500
            )
        }

        // Bento Contact Cards
        items(contacts.size) { index ->
            val contact = contacts[index]
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
                border = BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${contact.phone}")
                        }
                        context.startActivity(dialIntent)
                    }
                    .testTag("contact_item_$index")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = BentoBlueLight,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = contact.icon,
                                contentDescription = null,
                                tint = BentoPrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = BentoSlate900
                        )
                        Text(
                            text = contact.odiaName,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = BentoPrimaryBlue
                        )
                        Text(
                            text = "${contact.role} • ${contact.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoSlate500
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = BentoBlueLight,
                        modifier = Modifier.size(36.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${contact.phone}")
                                }
                                context.startActivity(dialIntent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = BentoPrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Transit & Commute Info Bento Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
                border = BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Balasore Transit & Connectivity",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = BentoSlate900
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    BentoTransitItem(
                        icon = Icons.Default.Train,
                        title = "Balasore Junction (BLS)",
                        desc = "A1-category station on Howrah–Chennai mainline. Direct superfast & Vande Bharat trains connect Kolkata, Bhubaneswar, Chennai, and Delhi."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BentoTransitItem(
                        icon = Icons.Default.DirectionsBus,
                        title = "Sahadevkhunta Central Bus Stand",
                        desc = "Frequent OSRTC and private deluxe buses operate to Bhubaneswar, Cuttack, Baripada, Digha, and Jaleswar."
                    )
                }
            }
        }

        // Play Store & Publication Info Bento Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoBlueLight),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("play_store_info_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = BentoPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google Play Store Ready",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = BentoPrimaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Version: 1.0.0 (Production Release ready)\n" +
                                "• Theme: Bento Grid System\n" +
                                "• Package: com.aistudio.balasore.kxnqtw\n" +
                                "• Target SDK: Android 15 (API 36)\n" +
                                "• Zero-permission photo picker & privacy compliant\n" +
                                "• Offline-first SQLite/Room database caching\n" +
                                "• Real-time Open-Meteo & IMD Coastal Weather integration\n" +
                                "• Dedicated to the people and travelers of Balasore, Odisha",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = BentoSlate700
                    )
                }
            }
        }
    }
}

@Composable
fun BentoTransitItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = BentoBlueLight,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BentoPrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = BentoSlate900
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = BentoSlate500
            )
        }
    }
}
