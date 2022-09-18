package com.footprint.footprint.domain.usecase

import com.footprint.footprint.domain.repository.CourseRepository

class GetSelfCourseListUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke() = repository.getSelfCourseList()
}