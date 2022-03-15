package com.footprint.footprint.data.remote.walk

data class WriteWalkResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: String
)
