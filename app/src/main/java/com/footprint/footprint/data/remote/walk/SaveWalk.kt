package com.footprint.footprint.data.remote.walk

data class SaveWalk(
    var startAt: String = "",
    var endAt: String = "",
    var distance: Double = 0.0,
    var coordinates: List<List<Double>> = listOf(),
    var calorie: Int = 0,
    var photoMatchNumList: ArrayList<Int> = arrayListOf()
)
