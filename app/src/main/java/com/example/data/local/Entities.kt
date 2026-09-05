package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val summary: String,
    val content: String,
    val category: String,
    val source: String,
    val publishedAt: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isBreaking: Boolean = false,
    val imageUrl: String? = null,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val id: Int = 1,
    val temperature: Double,
    val apparentTemperature: Double,
    val weatherCode: Int,
    val weatherDescription: String,
    val windSpeed: Double,
    val windGusts: Double,
    val humidity: Int,
    val alertLevel: String, // "NORMAL", "ADVISORY", "WARNING", "CYCLONE_ALERT"
    val alertTitle: String,
    val alertMessage: String,
    val tideState: String, // "RECEDING", "LOW_TIDE", "INCOMING", "HIGH_TIDE"
    val tideDescription: String,
    val sunrise: String,
    val sunset: String,
    val uvIndex: Double,
    val maxTemp: Double,
    val minTemp: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "tourism_hotspots")
data class HotspotEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val odiaName: String,
    val category: String, // "Beach", "Temple", "Wildlife", "Heritage", "Port"
    val shortDescription: String,
    val fullDescription: String,
    val highlights: String, // comma-separated
    val distanceKmFromBls: Int,
    val bestTimeToVisit: String,
    val timings: String,
    val entryFee: String,
    val specialty: String,
    val localTip: String,
    val latitude: Double = 21.4934,
    val longitude: Double = 86.9325,
    val isFavorite: Boolean = false
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String, // email or generated ID
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val phoneNumber: String = "",
    val locality: String = "Balasore Town",
    val bio: String = "Balasore Explorer & Resident",
    val avatarUri: String? = null,
    val securityQuestion: String = "What is your favorite place in Balasore?",
    val securityAnswer: String = "Chandipur",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetType: String, // "HOTSPOT", "NEWS", "WEATHER"
    val targetId: String,   // hotspot ID, news article ID (as String), or "coastal_weather_alert"
    val targetTitle: String,
    val userId: String,
    val userName: String,
    val userLocality: String = "Balasore",
    val userAvatarUri: String? = null,
    val rating: Int,        // 1 to 5 stars
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)
