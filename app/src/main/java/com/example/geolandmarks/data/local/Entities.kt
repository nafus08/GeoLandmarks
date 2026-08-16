package com.example.geolandmarks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "landmarks")
data class LandmarkEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val lat: Double,
    val lng: Double,
    val imageUrl: String,
    val score: Double,
    val visitCount: Int,
    val avgDistance: Double,
    val isDeleted: Boolean
)

@Entity(tableName = "visit_logs")
data class VisitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landmarkId: Int,
    val title: String,
    val timestamp: Long,
    val lat: Double,
    val lng: Double,
    val status: String, // "pending", "syncing", "done", "failed"
    val jobId: Int = -1,
    val distance: Double? = null
)
