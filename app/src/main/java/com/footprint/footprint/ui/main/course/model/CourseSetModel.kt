package com.footprint.footprint.ui.main.course.model

import com.naver.maps.geometry.LatLng

data class CourseSetModel(
    var selectedCoords: MutableList<LatLng> = mutableListOf(),
    var leftValue: Float = 0f,
    var rightValue: Float = 100f,
    var isSelected: Boolean = false
)