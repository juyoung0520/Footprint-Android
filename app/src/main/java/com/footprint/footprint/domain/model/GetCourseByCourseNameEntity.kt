package com.footprint.footprint.domain.model

data class GetCourseByCourseNameEntity(
    val courseName: String,
    val address: String,
    val allHashtags: List<CourseHashtagEntity>,
    val courseIdx: Int,
    val courseImg: String,
    val courseTime: Int,
    val description: String,
    val distance: Double,
    val selectedHashtags: List<CourseHashtagEntity>,
    val walkIdx: Int
)