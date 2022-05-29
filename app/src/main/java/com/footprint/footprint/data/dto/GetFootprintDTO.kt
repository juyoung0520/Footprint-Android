package com.footprint.footprint.data.dto

data class GetFootprintDTO(
    val footprintIdx: Int,
    val recordAt: String,
    val write: String,
    val photoList: List<String>,
    val tagList: List<String>,
    val onWalk: Int
)