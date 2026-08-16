package com.example.geolandmarks

import android.app.Application
import androidx.work.*
import com.example.geolandmarks.worker.SyncWorker
import java.util.concurrent.TimeUnit

class GeoLandmarksApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val osmConfig = org.osmdroid.config.Configuration.getInstance()
        // Use a more detailed user agent to comply with OSM policy
        osmConfig.userAgentValue = "GeoLandmarksApp-CSE489-v5-Student-24141057"
        
        // Ensure the cache is in a safe place
        val osmCache = java.io.File(cacheDir, "osmdroid")
        if (!osmCache.exists()) osmCache.mkdirs()
        osmConfig.osmdroidBasePath = osmCache
        osmConfig.osmdroidTileCache = java.io.File(osmCache, "tiles")
        
        osmConfig.load(this, getSharedPreferences("osmdroid", 0))
        setupSyncWorker()
    }

    private fun setupSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "LandmarkSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
