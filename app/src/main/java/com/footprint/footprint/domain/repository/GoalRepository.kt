package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.model.Goal

interface GoalRepository {
    suspend fun getThisMonthGoal(): Result<Goal>
}