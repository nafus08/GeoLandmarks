package com.example.geolandmarks.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.geolandmarks.data.local.AppDatabase
import com.example.geolandmarks.data.local.LandmarkEntity
import com.example.geolandmarks.data.local.VisitLogEntity
import com.example.geolandmarks.data.repository.LandmarkRepository
import com.example.geolandmarks.worker.VisitWorker
import androidx.work.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class LandmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LandmarkRepository
    val allLandmarks: LiveData<List<LandmarkEntity>>
    val allVisitLogs: LiveData<List<VisitLogEntity>>

    init {
        val landmarkDao = AppDatabase.getDatabase(application).landmarkDao()
        repository = LandmarkRepository(landmarkDao)
        allLandmarks = repository.allLandmarks
        allVisitLogs = repository.allVisitLogs
        refreshLandmarks()
    }

    fun refreshLandmarks() {
        viewModelScope.launch {
            repository.refreshLandmarks()
        }
    }

    fun visitLandmark(landmarkId: Int, title: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            val visitLogId = repository.addVisitLog(landmarkId, title, lat, lng)
            
            val inputData = Data.Builder()
                .putLong("visit_log_id", visitLogId)
                .build()
            
            val visitRequest = OneTimeWorkRequestBuilder<VisitWorker>()
                .setInputData(inputData)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            
            WorkManager.getInstance(getApplication()).enqueue(visitRequest)
        }
    }

    fun addLandmark(
        title: String,
        lat: Double,
        lng: Double,
        score: Double,
        imagePart: MultipartBody.Part,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.uploadLandmark(title, lat, lng, score, imagePart)
            onResult(success)
            if (success) refreshLandmarks()
        }
    }
}
