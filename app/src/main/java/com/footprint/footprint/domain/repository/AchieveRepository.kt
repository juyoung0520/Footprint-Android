package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.model.Result
import com.footprint.footprint.data.dto.TMonth
import com.footprint.footprint.data.dto.Today

interface AchieveRepository {
    suspend fun getToday(): Result<Today>
    suspend fun getTmonth(): Result<TMonth>
}