package com.example.geolandmarks.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LandmarkDao {
    @Query("SELECT * FROM landmarks WHERE isDeleted = 0")
    fun getAllLandmarks(): LiveData<List<LandmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLandmarks(landmarks: List<LandmarkEntity>)

    @Query("DELETE FROM landmarks")
    suspend fun deleteAllLandmarks()

    @Query("SELECT * FROM visit_logs ORDER BY timestamp DESC")
    fun getAllVisitLogs(): LiveData<List<VisitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitLog(visitLog: VisitLogEntity): Long

    @Update
    suspend fun updateVisitLog(visitLog: VisitLogEntity)

    @Query("SELECT * FROM visit_logs WHERE status = 'pending' OR status = 'syncing'")
    suspend fun getUnsyncedVisits(): List<VisitLogEntity>
    
    @Query("SELECT * FROM visit_logs WHERE id = :id")
    suspend fun getVisitById(id: Long): VisitLogEntity?
}
