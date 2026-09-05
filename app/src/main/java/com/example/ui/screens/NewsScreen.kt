package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NewsArticleEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.UserEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.ReviewsSection
import kotlinx.coroutines.flow.Flow
import com.example.ui.theme.BentoBlueLight
import com.example.ui.theme.BentoBluePill
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardWhite
import com.example.ui.theme.BentoPrimaryBlue
import com.example.ui.theme.BentoRedBg
import com.example.ui.theme.BentoRedText
import com.example.ui.theme.BentoSlate400
import com.example.ui.theme.BentoSlate500
import com.example.ui.theme.BentoSlate700
import com.example.ui.theme.BentoSlate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    articles: List<NewsArticleEntity>,
    selectedCategory: String,
    searchQuery: String,
    selectedArticle: NewsArticleEntity?,
    currentUser: UserEntity? = null,
    onOpenAuth: () -> Unit = {},
    getReviewsForArticle: (String) -> Flow<List<ReviewEntity>> = { kotlinx.coroutines.flow.emptyFlow() },
    onSubmitArticleReview: (articleId: String, articleTitle: String, rating: Int, comment: String, guestName: String?) -> Unit = { _, _, _, _, _ -> },
    onCategorySelect: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onArticleSelect: (NewsArticleEntity?) -> Unit,
    onToggleBookmark: (NewsArticleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "All",
        "Civic & Transport",
        "Coastal & Tourism",
        "Weather Alert",
        "Culture & Heritage",
        "Education & Tech",
        "Saved"
    )

    val breakingNews = articles.firstOrNull { it.isBreaking }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Bento Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search Balasore news & bulletins...", color = BentoSlate400) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = BentoSlate400)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search", tint = BentoSlate400)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = BentoCardWhite,
                    unfocusedContainerColor = BentoCardWhite,
                    focusedIndicatorColor = BentoPrimaryBlue,
                    unfocusedIndicatorColor = BentoBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("news_search_input")
            )
        }

        // Breaking News Bento Tile
        if (breakingNews != null && searchQuery.isEmpty() && selectedCategory == "All") {
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
                    border = BorderStroke(1.dp, BentoRedBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onArticleSelect(breakingNews) }
                        .testTag("breaking_news_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BentoRedBg,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📢", fontSize = 16.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "BREAKING ALERT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                ),
                                color = BentoRedText
                            )
                            Text(
                                text = breakingNews.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = BentoSlate900,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
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

        // Section Heading
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "Search Results" else "$selectedCategory Updates",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BentoSlate900
                )
                Text(
                    text = "${articles.size} updates",
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoSlate500
                )
            }
        }

        // Articles List in Bento Grid Cards
        if (articles.isEmpty()) {
            item {
                BentoEmptyStateCard(
                    message = if (searchQuery.isNotEmpty()) "No articles matching '$searchQuery'" else "No news available in this category."
                )
            }
        } else {
            items(articles, key = { it.id }) { article ->
                BentoNewsCard(
                    article = article,
                    onClick = { onArticleSelect(article) },
                    onToggleBookmark = { onToggleBookmark(article) }
                )
            }
        }
    }

    // Article Detail Modal
    if (selectedArticle != null) {
        ArticleDetailSheet(
            article = selectedArticle,
            currentUser = currentUser,
            reviewsFlow = getReviewsForArticle(selectedArticle.id.toString()),
            onOpenAuth = onOpenAuth,
            onSubmitReview = { rating, comment, guestName ->
                onSubmitArticleReview(selectedArticle.id.toString(), selectedArticle.title, rating, comment, guestName)
            },
            onDismiss = { onArticleSelect(null) },
            onToggleBookmark = { onToggleBookmark(selectedArticle) }
        )
    }
}

/**
 * Bento News Card: Matches HTML "bg-white rounded-3xl p-5 border border-slate-100 shadow-sm"
 */
@Composable
fun BentoNewsCard(
    article: NewsArticleEntity,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
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
            .testTag("news_article_${article.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top Row: Category pill + timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BentoBlueLight
                ) {
                    Text(
                        text = article.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = BentoPrimaryBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = article.publishedAt,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = BentoSlate400
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Headline
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 22.sp
                ),
                color = BentoSlate900,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = BentoSlate500,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom row: Source + Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source: ${article.source}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = BentoPrimaryBlue
                )

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (article.isBookmarked) BentoPrimaryBlue else BentoSlate400
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailSheet(
    article: NewsArticleEntity,
    currentUser: UserEntity? = null,
    reviewsFlow: Flow<List<ReviewEntity>> = kotlinx.coroutines.flow.emptyFlow(),
    onOpenAuth: () -> Unit = {},
    onSubmitReview: (rating: Int, comment: String, guestName: String?) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit,
    onToggleBookmark: () -> Unit
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
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BentoBlueLight
                ) {
                    Text(
                        text = article.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = BentoPrimaryBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = BentoSlate500)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = BentoSlate900
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = article.source,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = BentoPrimaryBlue
                )
                Text(
                    text = article.publishedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoSlate400
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Highlight Bento Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BentoBlueLight,
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = BentoSlate700,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Full Article Body
            Text(
                text = article.content,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = BentoSlate900
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons (Share, Bookmark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, article.title)
                            putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.summary}\n\nRead more on Balasore Live App")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share News Article"))
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = BentoSlate700, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", color = BentoSlate700)
                }

                TextButton(
                    onClick = onToggleBookmark,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (article.isBookmarked) BentoPrimaryBlue else BentoSlate700,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (article.isBookmarked) "Saved" else "Save", color = BentoSlate700)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Community Feedback & Comments Section
            ReviewsSection(
                targetType = "NEWS",
                targetId = article.id.toString(),
                targetTitle = article.title,
                reviewsFlow = reviewsFlow,
                currentUser = currentUser,
                onOpenAuth = onOpenAuth,
                onSubmitReview = onSubmitReview
            )
        }
    }
}
