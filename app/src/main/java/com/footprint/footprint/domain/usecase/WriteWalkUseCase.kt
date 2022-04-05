package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Badge
import com.footprint.footprint.domain.repository.WalkRepository
import com.footprint.footprint.ui.walk.model.WalkUIModel

class WriteWalkUseCase(private val repository: WalkRepository) {
    suspend operator fun invoke(walk: WalkUIModel): Result<List<Badge>> = repository.writeWalk(walk)
}