package com.footprint.footprint.data.mapper

import com.footprint.footprint.data.dto.GoalModel
import com.footprint.footprint.domain.model.*

object GoalMapper {
    fun mapperToGoalEntity(month: String, updateGoalEntity: UpdateGoalEntity): GoalEntity = updateGoalEntity.run {
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