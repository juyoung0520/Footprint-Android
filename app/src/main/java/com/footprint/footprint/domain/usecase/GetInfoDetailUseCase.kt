package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.AchieveDetailResult
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.repository.AchieveRepository


class GetInfoDetailUseCase(private val repository: AchieveRepository) {
    suspend operator fun invoke(): Result<AchieveDetailResult> = repository.getInfoDetail()
}