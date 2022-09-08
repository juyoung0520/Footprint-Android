package com.footprint.footprint.data.dto

data class UpdateCourseReqDTO(
    val courseName: String,
    var courseImg: String = "",
    val coordinates: List<List<Double>>,
    var hashtags: List<CourseHashtagDTO> = listOf(),
    val address: String = "",
    val length: Double,
    val courseTime: Int,
    val courseIdx: Int,
    val description: String = ""
)