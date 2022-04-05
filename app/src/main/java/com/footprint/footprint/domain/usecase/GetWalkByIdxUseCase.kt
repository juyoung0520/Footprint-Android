package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Walk
import com.footprint.footprint.domain.repository.WalkRepository

class GetWalkByIdxUseCase(private val repository: WalkRepository) {
    suspend operator fun invoke(walkIdx: Int): Result<Walk> = repository.getWalkByIdx(walkIdx)
}