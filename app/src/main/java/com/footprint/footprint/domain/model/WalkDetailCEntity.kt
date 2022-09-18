package com.footprint.footprint.domain.model

import com.naver.maps.geometry.LatLng

data class WalkDetailCEntity(
    val walkIdx: Int,
    val walkTime: Int,
    val distance: Double,
    val coordinates: MutableList<MutableList<LatLng>>,
    val hashtags: List<CourseHashtagEntity>,
    val photos: List<String>
)