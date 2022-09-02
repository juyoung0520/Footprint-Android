package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.CourseRepository

class GetMarkedCoursesUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(): Result<List<CourseDTO>> = repository.getMarkedCourses()
}