package com.footprint.footprint.data.dto

data class UpdateCourseReqDTO(
    val courseIdx: Int,
    val courseName: String,
    val courseImg: String = "",
    val hashtags: List<CourseHashtagDTO> = listOf(),
    val address: String,
    val length: Double,
    val courseTime: Int,
    val walkIdx: Int,
    val description: String = ""
)