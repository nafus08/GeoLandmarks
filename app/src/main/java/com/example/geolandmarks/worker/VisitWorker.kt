package com.example.geolandmarks.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.geolandmarks.data.local.AppDatabase
import com.example.geolandmarks.data.remote.RetrofitClient
import com.example.geolandmarks.data.remote.VisitBody
import kotlinx.coroutines.delay

class VisitWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val visitLogId = inputData.getLong("visit_log_id", -1L)
        if (visitLogId == -1L) return Result.failure()

        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.landmarkDao()
        val visitLog = dao.getVisitById(visitLogId) ?: return Result.failure()

        val api = RetrofitClient.api
        val projectKey = "24141057"

        return try {
            // Update status to syncing
            dao.updateVisitLog(visitLog.copy(status = "syncing"))

            // POST visit
            val visitResponse = api.postVisit(
                key = projectKey,
                body = VisitBody(
                    landmarkId = visitLog.landmarkId,
                    userLat = visitLog.lat,
                    userLon = visitLog.lng
                )
            )

            val jobId = visitResponse.jobId
            if (jobId <= 0) {
                dao.updateVisitLog(visitLog.copy(status = "failed"))
                return Result.failure()
            }
            
            dao.updateVisitLog(visitLog.copy(status = "syncing", jobId = jobId))

            // Poll for status
            var status = visitResponse.status
            var distance: Double? = null
            var attempts = 0
            while (status != "done" && attempts < 20) {
                delay(5000) // Poll every 5 seconds
                val jobStatus = api.getJobStatus(key = projectKey, jobId = jobId)
                status = jobStatus.status
                if (status == "done") {
                    distance = jobStatus.distance
                }
                attempts++
            }

            if (status == "done") {
                dao.updateVisitLog(visitLog.copy(status = "done", jobId = jobId, distance = distance))
                Result.success()
            } else {
                dao.updateVisitLog(visitLog.copy(status = "failed", jobId = jobId))
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dao.updateVisitLog(visitLog.copy(status = "failed"))
            Result.retry()
        }
    }
}
