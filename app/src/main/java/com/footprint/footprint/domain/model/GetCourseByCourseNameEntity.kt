package com.footprint.footprint.domain.model

import com.naver.maps.geometry.LatLng

data class GetCourseByCourseNameEntity(
    val courseName: String,
    val address: String,
    val allHashtags: List<CourseHashtagEntity>,
    val coordinates: List<List<LatLng>>,
    val courseIdx: Int,
    val courseImg: String,
    val courseTime: Int,
    val description: String,
    val distance: Double,
    val selectedHashtags: List<CourseHashtagEntity>,
    val walkIdx: Int
)