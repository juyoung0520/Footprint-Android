package com.footprint.footprint.domain.model

data class GetCourseByCourseNameEntity(
    val courseIdx: Int,
    val address: String,
    val distance: Double,
    val courseTime: Int,
    val photo: String,
    val courseName: String,
    val hashtags: List<CourseHashtagEntity>,
    val description: String
)
