package com.footprint.footprint.data.dto

data class WalkDetailCDTO(
    val walkIdx: Int,
    val walkTime: Int,
    val distance: Double,
    val coordinates: List<ArrayList<Double>>,
    val hashtags: List<CourseHashtagDTO>,
    val photos: ArrayList<String>
)