package com.footprint.footprint.data.model

data class WalkModel(
    var walkIdx: Int = 0,
    var walkTitle: String = "", //00번째 산책
    var walkTime: String = "",  //산책 시간
    var startAt: String = "",
    var endAt: String = "",
    var distance: Double = 0.0,
    var coordinate: List<List<Double>> = arrayListOf(),
    var calorie: Int = 0,
    var pathImg: String = "",
    var footprints: ArrayList<FootprintModel> = arrayListOf()
)
