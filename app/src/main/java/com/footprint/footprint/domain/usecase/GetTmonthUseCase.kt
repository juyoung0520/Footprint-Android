package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.TMonth
import com.footprint.footprint.domain.repository.AchieveRepository


class GetTmonthUseCase(private val repository: AchieveRepository)  {
    suspend fun invoke(): Result<TMonth> = repository.getTmonth()
}