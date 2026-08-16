package com.example.geolandmarks.data.remote

import com.google.gson.annotations.SerializedName

data class LandmarkDto(
    val id: Int,
    val title: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?,
    @SerializedName("image") val image: String?,
    val score: Double?,
    @SerializedName("visit_count") val visitCount: Int?,
    @SerializedName("avg_distance") val avgDistance: Double?,
    @SerializedName("is_active") val isActive: Int?
)

data class VisitResponse(
    @SerializedName("job_id") val jobId: Int,
    val status: String,
    val error: String? = null
)

data class JobStatusResponse(
    val status: String,
    val distance: Double? = null
)

data class AddLandmarkResponse(
    val status: String,
    val message: String
)
