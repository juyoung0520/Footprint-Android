package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.MonthDayDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.WalkRepository

class GetMonthWalksUseCase(private val repository: WalkRepository) {
    suspend operator fun invoke(year: Int, month: Int): Result<List<MonthDayDTO>> = repository.getMonthWalks(year, month)
}