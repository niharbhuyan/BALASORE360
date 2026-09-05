package com.example.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

object SyncManager {

    /**
     * Schedules periodic background sync using WorkManager to cache news, weather,
     * and tourism data in the Room database every 15 minutes when network is available.
     */
    fun schedulePeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DataSyncWorker.WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    /**
     * Triggers an immediate one-time sync with WorkManager to refresh local Room cache.
     */
    fun triggerImmediateSync(context: Context) {
        val immediateRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                1,
                TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            DataSyncWorker.WORK_NAME_ONE_TIME,
            ExistingWorkPolicy.REPLACE,
            immediateRequest
        )
    }

    /**
     * Observes the status of the immediate sync worker to update UI loading indicators.
     */
    fun observeImmediateSync(context: Context): Flow<WorkInfo?> {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkFlow(DataSyncWorker.WORK_NAME_ONE_TIME)
            .map { list -> list.firstOrNull() }
    }

    /**
     * Formats timestamp into a user-friendly relative or clock string.
     */
    fun formatSyncTime(timestamp: Long): String {
        if (timestamp <= 0L) return "Never"
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / (1000 * 60)
        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            minutes < 1440 -> "${minutes / 60}h ago"
            else -> "${minutes / 1440}d ago"
        }
    }
}
