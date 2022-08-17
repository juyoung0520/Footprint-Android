package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.GoalEntity
import com.footprint.footprint.domain.repository.GoalRepository

class GetThisMonthGoalUseCase(private val repository: GoalRepository) {
    suspend fun invoke(): Result<GoalEntity> = repository.getThisMonthGoal()
}