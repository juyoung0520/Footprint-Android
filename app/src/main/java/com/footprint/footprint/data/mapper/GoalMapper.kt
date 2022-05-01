package com.footprint.footprint.data.mapper

import com.footprint.footprint.domain.model.Goal
import com.footprint.footprint.domain.model.UpdateGoal
import com.footprint.footprint.domain.model.UserGoalTime

object GoalMapper {
    fun mapperToGoal(month: String, updateGoal: UpdateGoal): Goal {
        return Goal(
            month = month,
            dayIdx = updateGoal.dayIdx,
            userGoalTime = UserGoalTime(
                walkGoalTime = updateGoal.walkGoalTime,
                walkTimeSlot = updateGoal.walkTimeSlot
            ),
            goalNextModified = true
        )
    }
}