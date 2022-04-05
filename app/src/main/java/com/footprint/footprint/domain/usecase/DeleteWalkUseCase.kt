package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.WalkRepository

class DeleteWalkUseCase(private val repository: WalkRepository) {
    suspend operator fun invoke(walkIdx: Int): Result<BaseResponse> = repository.deleteWalk(walkIdx)
}