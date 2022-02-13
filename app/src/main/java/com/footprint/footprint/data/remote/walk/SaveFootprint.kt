package com.footprint.footprint.data.remote.walk

data class SaveFootprint(
    var coordinates: List<Double> = listOf(),
    var recordAt: String = "",
    var write: String = "",
    var hashtagList: List<String>? = null,
    var onWalk: Int = 1
)
