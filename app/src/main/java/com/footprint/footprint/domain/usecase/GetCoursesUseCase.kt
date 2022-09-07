package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.BadgeInfo
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.domain.repository.BadgeRepository
import com.footprint.footprint.domain.repository.CourseRepository

class GetCoursesUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(bounds: BoundsModel): Result<List<CourseDTO>> = repository.getCourses(bounds)
}