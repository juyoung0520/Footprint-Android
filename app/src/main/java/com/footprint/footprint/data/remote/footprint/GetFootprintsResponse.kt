package com.footprint.footprint.data.remote.footprint

data class GetFootprintsResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<Footprint>
)

data class Footprint(
    val footprintIdx: Int,
    val recordAt: String,
    val write: String,
    val photoList: List<String>,
    val tagList: List<String>,
    val onWalk: Int
)
