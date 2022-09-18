package com.footprint.footprint.domain.usecase

import com.footprint.footprint.domain.repository.CourseRepository

class GetWalkDetailCUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(walkNumber: Int) = repository.getWalkDetailC(walkNumber)
}