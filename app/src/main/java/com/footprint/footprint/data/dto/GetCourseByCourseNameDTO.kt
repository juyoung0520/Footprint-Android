package com.footprint.footprint.data.dto

data class GetCourseByCourseNameDTO(
    val address: String,
    val allHashtags: List<CourseHashtagDTO>,
    val courseIdx: Int,
    val courseImg: String,
    val courseTime: Int,
    val description: String,
    val distance: Double,
    val selectedHashtags: List<CourseHashtagDTO>,
    val walkIdx: Int
)