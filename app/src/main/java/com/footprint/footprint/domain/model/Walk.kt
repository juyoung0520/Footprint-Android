package com.footprint.footprint.domain.model

data class Walk(
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

data class WriteWalkReq(
    var startAt: String = "",
    var endAt: String = "",
    var distance: Double = 0.0,
    var coordinates: List<List<Double>> = listOf(),
    var calorie: Int = 0,
    var photoMatchNumList: ArrayList<Int> = arrayListOf()
)