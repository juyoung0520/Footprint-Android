package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.CourseRepository

class EvaluateCourseUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(courseIdx: Int, evaluate: Int): Result<BaseResponse> = repository.evaluateCourse(courseIdx, evaluate)
}