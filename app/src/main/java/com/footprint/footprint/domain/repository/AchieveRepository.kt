package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.TMonth
import com.footprint.footprint.data.dto.Today
import com.footprint.footprint.data.dto.AchieveDetailResult

interface AchieveRepository {
    suspend fun getToday(): Result<Today>
    suspend fun getTmonth(): Result<TMonth>
    suspend fun getInfoDetail(): Result<AchieveDetailResult>
}