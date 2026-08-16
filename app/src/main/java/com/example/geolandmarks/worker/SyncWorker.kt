package com.example.geolandmarks.worker

import android.content.Context
import androidx.work.*
import com.example.geolandmarks.data.local.AppDatabase
import com.example.geolandmarks.data.repository.LandmarkRepository

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = LandmarkRepository(db.landmarkDao())

        return try {
            repository.refreshLandmarks()
            
            // Also check for unsynced visits and enqueue VisitWorker for them
            val unsynced = db.landmarkDao().getUnsyncedVisits()
            unsynced.forEach { visit ->
                val inputData = Data.Builder()
                    .putLong("visit_log_id", visit.id)
                    .build()
                
                val visitRequest = OneTimeWorkRequestBuilder<VisitWorker>()
                    .setInputData(inputData)
                    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .build()
                
                WorkManager.getInstance(applicationContext).enqueue(visitRequest)
            }
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
