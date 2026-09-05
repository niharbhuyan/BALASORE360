package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_articles ORDER BY isBreaking DESC, timestamp DESC")
    fun getAllNews(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE category = :category ORDER BY timestamp DESC")
    fun getNewsByCategory(category: String): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedNews(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE isBreaking = 1 ORDER BY timestamp DESC LIMIT 3")
    fun getBreakingNews(): Flow<List<NewsArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticleEntity>)

    @Query("UPDATE news_articles SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getCount(): Int

    @Query("DELETE FROM news_articles WHERE isBookmarked = 0")
    suspend fun clearNonBookmarked()
}

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_cache WHERE id = 1 LIMIT 1")
    fun getWeatherCache(): Flow<WeatherCacheEntity?>

    @Query("SELECT * FROM weather_cache WHERE id = 1 LIMIT 1")
    suspend fun getWeatherCacheSync(): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWeather(weather: WeatherCacheEntity)
}

@Dao
interface HotspotDao {
    @Query("SELECT * FROM tourism_hotspots ORDER BY distanceKmFromBls ASC")
    fun getAllHotspots(): Flow<List<HotspotEntity>>

    @Query("SELECT * FROM tourism_hotspots WHERE category = :category ORDER BY distanceKmFromBls ASC")
    fun getHotspotsByCategory(category: String): Flow<List<HotspotEntity>>

    @Query("SELECT * FROM tourism_hotspots WHERE isFavorite = 1")
    fun getFavoriteHotspots(): Flow<List<HotspotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotspots(hotspots: List<HotspotEntity>)

    @Query("UPDATE tourism_hotspots SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM tourism_hotspots")
    suspend fun getCount(): Int
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET avatarUri = :avatarUri WHERE id = :userId")
    suspend fun updateAvatar(userId: String, avatarUri: String?)

    @Query("UPDATE users SET passwordHash = :newPassword WHERE email = :email")
    suspend fun updatePassword(email: String, newPassword: String): Int

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getCount(): Int
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE targetType = :targetType AND targetId = :targetId ORDER BY timestamp DESC")
    fun getReviewsForTarget(targetType: String, targetId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE targetType = :targetType ORDER BY timestamp DESC")
    fun getReviewsByType(targetType: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Query("SELECT AVG(rating) FROM reviews WHERE targetType = :targetType AND targetId = :targetId")
    fun getAverageRating(targetType: String, targetId: String): Flow<Double?>

    @Query("SELECT COUNT(*) FROM reviews WHERE targetType = :targetType AND targetId = :targetId")
    fun getReviewCount(targetType: String, targetId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("SELECT COUNT(*) FROM reviews")
    suspend fun getCount(): Int
}
