package com.footprint.footprint.data.dto

data class GetCourseByCourseNameDTO(
    val courseIdx: Int,
    val walkIdx: Int,
    val courseTime: Int,
    val distance: Double,
    val coordinates: List<ArrayList<Double>>,
    val hashtags: List<CourseHashtagDTO>,
    val photos: ArrayList<String>
)
