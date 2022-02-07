package com.footprint.footprint.data.model

import java.io.Serializable

data class GoalModel(
    var month: String ?= "",
    val dayIdx: ArrayList<Int> = arrayListOf(),
    val userGoalTime: UserGoalTime = UserGoalTime(),
    var goalNextModified: Boolean ?= true
): Serializable

data class UserGoalTime(
    var walkGoalTime: Int? = null,
    var walkTimeSlot: Int? = null
): Serializable
