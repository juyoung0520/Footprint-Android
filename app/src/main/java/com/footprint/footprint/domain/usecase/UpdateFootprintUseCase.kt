package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.FootprintRepository

class UpdateFootprintUseCase(private val repository: FootprintRepository) {
    suspend operator fun invoke(walkIdx: Int, footprintIdx: Int, footprintMap: HashMap<String, Any>, footprintPhoto: List<String>?): Result<BaseResponse> = repository.updateFootprint(walkIdx, footprintIdx, footprintMap, footprintPhoto)
}