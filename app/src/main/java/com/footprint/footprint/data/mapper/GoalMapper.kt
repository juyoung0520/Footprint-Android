package com.footprint.footprint.data.mapper

import com.footprint.footprint.data.dto.GoalModel
import com.footprint.footprint.domain.model.*

object GoalMapper {
    fun mapperToGoalEntity(month: String, updateGoal: UpdateGoal): GoalEntity = updateGoal.run {
        GoalEntity(month, dayIdx, UserGoalTime(walkGoalTime, walkTimeSlot), true)
    }

    fun mapperToGoalEntity(goalModel: GoalModel): GoalEntity = goalModel.run {
        GoalEntity(
            month,
            dayIdx,
            userGoalTime.run { UserGoalTime(walkGoalTime, walkTimeSlot) },
            goalNextModified
        )
    }
}