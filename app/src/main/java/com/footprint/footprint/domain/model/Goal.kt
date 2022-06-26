package com.footprint.footprint.domain.model

data class UpdateGoal(
    val dayIdx: ArrayList<Int> = arrayListOf(),
    var walkGoalTime: Int? = null,
    var walkTimeSlot: Int? = null
)
