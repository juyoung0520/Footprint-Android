package com.footprint.footprint.data.remote.walk

data class BaseResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Any
)
