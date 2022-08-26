package com.footprint.footprint.domain.model

import com.naver.maps.geometry.LatLng

data class CourseInfoModel(
    val idx: Int = 1,
    var previewImageUrl: String = "",
    var title: String = "",
    var distance: Double? = null, // Int?
    var time: Int? = null,
    var tags: List<String> = listOf(),
    var coords: List<List<LatLng>> = listOf(listOf()), // List<List<LatLng>> = listOf(listOf())
    var description: String = ""
)
