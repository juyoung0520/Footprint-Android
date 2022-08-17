package com.footprint.footprint.domain.model

data class GoalEntity(
    var month: String ?= "",
    val dayIdx: ArrayList<Int> = arrayListOf(),
    val userGoalTime: UserGoalTime = UserGoalTime(),
    var goalNextModified: Boolean? = true
)

data class UserGoalTime(
    var walkGoalTime: Int? = null,
    var walkTimeSlot: Int? = null
)
