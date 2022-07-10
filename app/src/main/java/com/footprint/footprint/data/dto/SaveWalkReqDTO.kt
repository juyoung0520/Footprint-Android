package com.footprint.footprint.data.dto

data class SaveWalkReqModel(
    val walk: WalkModel,
    val footprintList: List<FootprintModel> = listOf()
)

data class WalkModel(
    val startAt: String,
    val endAt: String,
    val distance: Double,
    val coordinates: List<List<Double>> = listOf(),
    val calorie: Int,
    val thumbnail: String
)

data class FootprintModel(
    val coordinates: List<Double> = listOf(),
    val recordAt: String,
    val write: String,
    val hashtagList: List<String> = listOf(),
    val photos: List<String> = listOf(),
    val onWalk: Int
)