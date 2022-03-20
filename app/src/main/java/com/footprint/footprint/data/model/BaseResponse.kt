package com.footprint.footprint.data.model

data class BaseResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: String
)
