package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.DayWalkDTO
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.repository.WalkRepository

class GetDayWalksUseCase(private val repository: WalkRepository) {
    suspend operator fun invoke(date: String): Result<List<DayWalkDTO>> = repository.getDayWalks(date)
}