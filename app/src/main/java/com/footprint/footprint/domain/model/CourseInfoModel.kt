package com.footprint.footprint.domain.model

// 널 처리 수정
data class CourseInfoModel(
    var previewImageUrl: String = "",
    var title: String = "",
    var distance: Int ?= null,
    var time: Int ?= null,
    var tags: List<String> = listOf(),
    var description: String = ""
)
