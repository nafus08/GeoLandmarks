package com.example.geolandmarks.data.repository

import androidx.lifecycle.LiveData
import com.example.geolandmarks.data.local.LandmarkDao
import com.example.geolandmarks.data.local.LandmarkEntity
import com.example.geolandmarks.data.local.VisitLogEntity
import com.example.geolandmarks.data.remote.LandmarkApi
import com.example.geolandmarks.data.remote.LandmarkIdBody
import com.example.geolandmarks.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class LandmarkRepository(private val landmarkDao: LandmarkDao) {

    private val api = RetrofitClient.api
    private val projectKey = "24141057"
    private val imageBaseUrl = "https://labs.anontech.info/cse489/exm3/"

    val allLandmarks: LiveData<List<LandmarkEntity>> = landmarkDao.getAllLandmarks()
    val allVisitLogs: LiveData<List<VisitLogEntity>> = landmarkDao.getAllVisitLogs()

    suspend fun refreshLandmarks() {
        withContext(Dispatchers.IO) {
            try {
                val remoteLandmarks = api.getLandmarks(key = projectKey)
                val entities = remoteLandmarks.map {
                    LandmarkEntity(
                        id = it.id,
                        title = it.title ?: "Unknown",
                        lat = it.lat ?: 0.0,
                        lng = it.lon ?: 0.0,
                        imageUrl = if (it.image?.startsWith("http") == true) it.image else imageBaseUrl + it.image,
                        score = it.score ?: 0.0,
                        visitCount = it.visitCount ?: 0,
                        avgDistance = it.avgDistance ?: 0.0,
                        isDeleted = it.isActive == 0
                    )
                }
                landmarkDao.insertLandmarks(entities)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addVisitLog(landmarkId: Int, title: String, lat: Double, lng: Double): Long {
        val visitLog = VisitLogEntity(
            landmarkId = landmarkId,
            title = title,
            timestamp = System.currentTimeMillis(),
            lat = lat,
            lng = lng,
            status = "pending"
        )
        return landmarkDao.insertVisitLog(visitLog)
    }

    suspend fun uploadLandmark(
        title: String,
        lat: Double,
        lng: Double,
        score: Double,
        imagePart: MultipartBody.Part
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.addLandmark(
                    key = projectKey,
                    title = title.toRequestBody(),
                    lat = lat.toString().toRequestBody(),
                    lon = lng.toString().toRequestBody(),
                    score = score.toString().toRequestBody(),
                    image = imagePart
                )
                response.status == "success"
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun deleteLandmark(landmarkId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.deleteLandmark(key = projectKey, body = LandmarkIdBody(landmarkId))
                response.isSuccessful
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun restoreLandmark(landmarkId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.restoreLandmark(key = projectKey, body = LandmarkIdBody(landmarkId))
                response.isSuccessful
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
