package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ReviewEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardWhite
import com.example.ui.theme.BentoGold
import com.example.ui.theme.BentoPrimaryBlue
import com.example.ui.theme.BentoSlate100
import com.example.ui.theme.BentoSlate500
import com.example.ui.theme.BentoSlate700
import com.example.ui.theme.BentoSlate900
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReviewsSection(
    targetType: String,
    targetId: String,
    targetTitle: String,
    reviewsFlow: Flow<List<ReviewEntity>>,
    currentUser: UserEntity?,
    onOpenAuth: () -> Unit,
    onSubmitReview: (rating: Int, comment: String, guestName: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val reviews by reviewsFlow.collectAsState(initial = emptyList())
    val averageRating = if (reviews.isNotEmpty()) {
        reviews.map { it.rating }.average()
    } else 0.0

    var isWritingReview by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableIntStateOf(5) }
    var commentText by remember { mutableStateOf("") }
    var guestNameText by remember { mutableStateOf("") }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = BentoCardWhite,
        border = BorderStroke(1.dp, BentoBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Title & Average Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "COMMUNITY REVIEWS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = BentoPrimaryBlue
                    )
                    Text(
                        text = "Feedback & Ratings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = BentoSlate900
                    )
                }

                // Average Rating Badge
                if (reviews.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BentoGold.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, BentoGold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = BentoGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(Locale.US, "%.1f", averageRating),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = BentoSlate900
                            )
                            Text(
                                text = " (${reviews.size})",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoSlate500
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Write a review toggle button
            if (!isWritingReview) {
                OutlinedButton(
                    onClick = { isWritingReview = true },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BentoPrimaryBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("write_review_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = null,
                        tint = BentoPrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rate & Share Your Experience",
                        color = BentoPrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Expandable Review Form
            AnimatedVisibility(visible = isWritingReview) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BentoSlate100.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "How was your experience?",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = BentoSlate900
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Star selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.testTag("star_rating_selector")
                        ) {
                            for (star in 1..5) {
                                Icon(
                                    imageVector = if (star <= selectedRating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "$star Stars",
                                    tint = if (star <= selectedRating) BentoGold else BentoSlate500,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .padding(2.dp)
                                        .clickable { selectedRating = star }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (selectedRating) {
                                    5 -> "Exceptional (5★)"
                                    4 -> "Very Good (4★)"
                                    3 -> "Average (3★)"
                                    2 -> "Disappointing (2★)"
                                    else -> "Poor (1★)"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = BentoSlate700
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Review comment field
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Write your review, helpful travel tip, or observation...") },
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BentoPrimaryBlue,
                                unfocusedBorderColor = BentoBorder,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("review_comment_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // User status: Logged in as or guest name
                        if (currentUser != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = BentoPrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Posting as ${currentUser.fullName} (${currentUser.locality})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = BentoSlate700
                                )
                            }
                        } else {
                            Column {
                                OutlinedTextField(
                                    value = guestNameText,
                                    onValueChange = { guestNameText = it },
                                    label = { Text("Your Name or Balasore Locality") },
                                    placeholder = { Text("e.g. Ramesh / Remuna resident") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BentoPrimaryBlue,
                                        unfocusedBorderColor = BentoBorder,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Or Sign in to post with your verified Balasore profile",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoPrimaryBlue,
                                    modifier = Modifier
                                        .clickable { onOpenAuth() }
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Submit & Cancel buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { isWritingReview = false },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (commentText.isNotBlank()) {
                                        onSubmitReview(selectedRating, commentText, guestNameText.ifBlank { null })
                                        commentText = ""
                                        isWritingReview = false
                                    }
                                },
                                enabled = commentText.isNotBlank(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryBlue),
                                modifier = Modifier.testTag("submit_review_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Post Review")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Review List
            if (reviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Be the first to share your feedback or review for this $targetType!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BentoSlate500
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    reviews.forEach { review ->
                        ReviewItemCard(review = review)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItemCard(review: ReviewEntity) {
    val dateStr = remember(review.timestamp) {
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        sdf.format(Date(review.timestamp))
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BentoSlate100.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, BentoBorder.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar pill
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(BentoPrimaryBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.userName.take(2).uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = BentoPrimaryBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = review.userName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = BentoSlate900
                        )
                        Text(
                            text = review.userLocality,
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoSlate500
                        )
                    }
                }

                // Star rating & date
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= review.rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = null,
                                tint = if (i <= review.rating) BentoGold else BentoSlate500,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoSlate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = BentoSlate700,
                lineHeight = 20.sp
            )
        }
    }
}
