package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.GoalEntity
import com.footprint.footprint.domain.model.UpdateGoal

interface GoalRepository {
    suspend fun getThisMonthGoal(): Result<GoalEntity>
    suspend fun getNextMonthGoal(): Result<GoalEntity>
    suspend fun updateGoal(month: String, goal: UpdateGoal): Result<GoalEntity>
}