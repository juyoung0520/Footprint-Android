package com.footprint.footprint.domain.model

data class SelfCourseEntity(
    val walkIdx: Int,
    val walkTime: String,
    val pathImageUrl: String,
    val hashtag: ArrayList<String>
)