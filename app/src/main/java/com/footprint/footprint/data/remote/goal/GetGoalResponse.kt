package com.footprint.footprint.data.remote.goal

data class GetGoalResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: String
)
