package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Footprint
import com.footprint.footprint.domain.repository.FootprintRepository

class GetFootprintsByWalkIdxUseCase(private val repository: FootprintRepository) {
    suspend operator fun invoke(walkIdx: Int): Result<List<Footprint>> = repository.getFootprintsByWalkIdx(walkIdx)
}