package com.footprint.footprint.data.dto

data class GetSelfCourseDTO(
    val getWalks: List<SelfCourseDTO>
)

data class SelfCourseDTO (
    val userDateWalk: UserDateWalkDTO,
    val hashtag: ArrayList<String>
)