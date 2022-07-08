package com.footprint.footprint.domain.model

data class UpdateGoalEntity(
    val dayIdx: ArrayList<Int> = arrayListOf(),
    var walkGoalTime: Int? = null,
    var walkTimeSlot: Int? = null
)
