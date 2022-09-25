package com.footprint.footprint.domain.model

data class UpdateCourseReqEntity(
    val courseIdx: Int,
    var courseName: String,
    var courseImg: String = "",
    var hashtags: List<CourseHashtagEntity> = listOf(),
    val address: String,
    val length: Double,
    val courseTime: Int,
    val walkIdx: Int,
    var description: String = ""
)
