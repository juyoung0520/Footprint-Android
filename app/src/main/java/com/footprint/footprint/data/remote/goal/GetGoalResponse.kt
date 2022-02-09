package com.footprint.footprint.data.remote.goal

import com.footprint.footprint.data.model.GoalModel

data class GetGoalResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: GoalModel
)
