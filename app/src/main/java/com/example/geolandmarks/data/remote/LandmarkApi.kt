package com.example.geolandmarks.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface LandmarkApi {
    @GET("api.php")
    suspend fun getLandmarks(
        @Query("action") action: String = "get_landmarks",
        @Query("key") key: String
    ): List<LandmarkDto>

    @POST("api.php")
    suspend fun postVisit(
        @Query("action") action: String = "visit_landmark",
        @Query("key") key: String,
        @Body body: VisitBody
    ): VisitResponse

    @GET("api.php")
    suspend fun getJobStatus(
        @Query("action") action: String = "get_job_status",
        @Query("key") key: String,
        @Query("job_id") jobId: Int
    ): JobStatusResponse

    @Multipart
    @POST("api.php")
    suspend fun addLandmark(
        @Query("action") action: String = "create_landmark",
        @Query("key") key: String,
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part("score") score: RequestBody,
        @Part image: MultipartBody.Part
    ): AddLandmarkResponse

    @POST("api.php")
    suspend fun deleteLandmark(
        @Query("action") action: String = "delete_landmark",
        @Query("key") key: String,
        @Body body: LandmarkIdBody
    ): Response<Unit>

    @POST("api.php")
    suspend fun restoreLandmark(
        @Query("action") action: String = "restore_landmark",
        @Query("key") key: String,
        @Body body: LandmarkIdBody
    ): Response<Unit>
}

data class VisitBody(
    @SerializedName("landmark_id") val landmarkId: Int,
    @SerializedName("user_lat") val userLat: Double,
    @SerializedName("user_lon") val userLon: Double
)

data class LandmarkIdBody(
    @SerializedName("landmark_id") val landmarkId: Int
)
