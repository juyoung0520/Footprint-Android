package com.footprint.footprint.data.dto

data class GetWalkModel(
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