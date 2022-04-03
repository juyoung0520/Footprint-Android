package com.footprint.footprint.domain.model

data class Footprint(
    val footprintIdx: Int,
    val recordAt: String,
    val write: String,
    val photoList: List<String>,
    val tagList: List<String>,
    val onWalk: Int
)

data class WriteFootprintReq(
    var coordinates: List<Double> = listOf(),
    var recordAt: String = "",
    var write: String = "",
    var hashtagList: List<String>? = null,
    var onWalk: Int = 1
)