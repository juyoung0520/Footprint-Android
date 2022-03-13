package com.footprint.footprint.data.remote.walk

data class GetWalkResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: String
)

data class WalkInfoResponse(
    val walkIdx: Int,
    val getWalkTime: GetWalkTime,
    val calorie: Int,
    val distance: Double,
    val footCount: Int,
    val pathImageUrl: String
)

data class GetWalkTime(
    val date: String,
    val startAt: String,
    val endAt: String,
    val timeString: String
)
