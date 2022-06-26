package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.GoalEntity
import com.footprint.footprint.domain.model.UpdateGoal
import com.footprint.footprint.domain.repository.GoalRepository

class UpdateGoalUseCase(private val repository: GoalRepository) {
    suspend fun invoke(month: String, goal: UpdateGoal): Result<GoalEntity> = repository.updateGoal(month, goal)
}