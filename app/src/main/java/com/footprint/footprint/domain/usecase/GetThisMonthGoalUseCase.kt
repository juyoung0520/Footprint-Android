package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.model.Goal
import com.footprint.footprint.domain.repository.GoalRepository
import com.footprint.footprint.utils.RemoteErrorEmitter

class GetThisMonthGoalUseCase(private val repository: GoalRepository) {
    suspend fun invoke(): Result<Goal> = repository.getThisMonthGoal()
}