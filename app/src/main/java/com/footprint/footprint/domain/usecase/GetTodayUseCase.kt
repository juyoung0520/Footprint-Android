package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.Today
import com.footprint.footprint.domain.repository.AchieveRepository


class GetTodayUseCase(private val repository: AchieveRepository)  {
    suspend fun invoke(): Result<Today> = repository.getToday()
}