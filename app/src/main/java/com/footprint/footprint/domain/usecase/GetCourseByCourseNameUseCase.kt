package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.GetCourseByCourseNameEntity
import com.footprint.footprint.domain.repository.CourseRepository

class GetCourseByCourseNameUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(courseName: String): Result<GetCourseByCourseNameEntity> = repository.getCourseByCourseName(courseName)
}