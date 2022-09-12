package com.footprint.footprint.domain.model

import com.naver.maps.geometry.LatLng

data class UpdateCourseReqEntity(
    val courseIdx: Int,
    var courseName: String,
    var courseImg: String = "",
    val coordinates: List<List<LatLng>>,
    var hashtags: List<CourseHashtagEntity> = listOf(),
    val address: String,
    val length: Double,
    val courseTime: Int,
    val walkIdx: Int,
    var description: String = ""
)
