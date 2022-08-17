package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.TagWalksDTO
import com.footprint.footprint.domain.repository.WalkRepository

class GetTagWalksUseCase(private val repository: WalkRepository) {
    suspend operator fun invoke(tag: String): Result<List<TagWalksDTO>> = repository.getTagWalks(tag)
}