package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.data.repository.BalasoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Background WorkManager Worker responsible for caching News, Weather, and Tourism
 * data into the local Room database, ensuring the app remains fully functional offline.
 */
class DataSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "DataSyncWorker: Starting background Room caching for Balasore...")
        try {
            val repository = BalasoreRepository.getInstance(applicationContext)
            val syncResult = repository.syncAllData()

            val outputData = workDataOf(
                KEY_SYNC_SUCCESS to syncResult.isSuccess,
                KEY_WEATHER_UPDATED to syncResult.weatherUpdated,
                KEY_NEWS_COUNT to syncResult.newsArticlesCount,
                KEY_HOTSPOTS_COUNT to syncResult.hotspotsCount,
                KEY_TIMESTAMP to syncResult.timestamp,
                KEY_IS_OFFLINE to syncResult.isOfflineServed,
                KEY_MESSAGE to syncResult.message
            )

            Log.d(TAG, "DataSyncWorker finished: ${syncResult.message}")
            Result.success(outputData)
        } catch (e: Exception) {
            Log.e(TAG, "DataSyncWorker failed with exception", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                val failureData = workDataOf(
                    KEY_SYNC_SUCCESS to false,
                    KEY_MESSAGE to (e.message ?: "Background cache sync failed")
                )
                Result.failure(failureData)
            }
        }
    }

    companion object {
        const val TAG = "DataSyncWorker"
        const val WORK_NAME_PERIODIC = "BalasorePeriodicDataSync"
        const val WORK_NAME_ONE_TIME = "BalasoreImmediateDataSync"

        const val KEY_SYNC_SUCCESS = "sync_success"
        const val KEY_WEATHER_UPDATED = "weather_updated"
        const val KEY_NEWS_COUNT = "news_count"
        const val KEY_HOTSPOTS_COUNT = "hotspots_count"
        const val KEY_TIMESTAMP = "timestamp"
        const val KEY_IS_OFFLINE = "is_offline"
        const val KEY_MESSAGE = "message"
    }
}
