package com.footprint.footprint.domain.model

import com.naver.maps.geometry.LatLng

// 널 처리 수정, 임시로 만들어놓음
data class CourseInfoModel(
    var previewImageUrl: String = "",
    var title: String = "",
    var distance: Int? = null,
    var time: Int? = null,
    var tags: List<String> = listOf(),
    var coords: List<List<LatLng>> = listOf(listOf()),
    var description: String = ""
)
