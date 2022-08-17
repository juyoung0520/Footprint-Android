package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.GetFootprintEntity
import com.footprint.footprint.domain.repository.FootprintRepository

class GetFootprintsByWalkIdxUseCase(private val repository: FootprintRepository) {
    suspend operator fun invoke(walkIdx: Int): Result<List<GetFootprintEntity>> = repository.getFootprintsByWalkIdx(walkIdx)
}