package com.footprint.footprint.domain.model

import com.google.gson.annotations.SerializedName
import com.naver.maps.geometry.LatLng

data class CourseInfoModel(
        val idx: Int,
        val previewImageUrl: String,
        val title: String,
        val distance: Double,
        val time: Int,
        val courseCount: Int,
        val courseLike: Int,
        val tags: List<String> = listOf(),
        val coords: List<List<LatLng>>,
        val description: String
)
