package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.BadgeEntity
import com.footprint.footprint.domain.model.SaveWalkEntity
import com.footprint.footprint.domain.repository.WalkRepository

class SaveWalkUseCase(private val repository: WalkRepository) {
    suspend operator fun invoke(request: SaveWalkEntity): Result<List<BadgeEntity>> = repository.saveWalk(request)
}