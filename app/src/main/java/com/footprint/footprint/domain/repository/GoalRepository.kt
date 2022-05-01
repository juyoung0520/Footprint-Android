package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Goal
import com.footprint.footprint.domain.model.UpdateGoal

interface GoalRepository {
    suspend fun getThisMonthGoal(): Result<Goal>
    suspend fun getNextMonthGoal(): Result<Goal>
    suspend fun updateGoal(month: String, goal: UpdateGoal): Result<Goal>
}