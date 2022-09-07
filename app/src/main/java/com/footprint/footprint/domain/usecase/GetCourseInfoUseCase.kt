package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.CourseInfoDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.CourseRepository

class GetCourseInfoUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(courseIdx: Int): Result<CourseInfoDTO> = repository.getCourseInfo(courseIdx)
}