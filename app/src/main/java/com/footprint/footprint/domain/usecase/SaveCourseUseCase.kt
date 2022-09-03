package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.RecommendEntity
import com.footprint.footprint.domain.repository.CourseRepository

class SaveCourseUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(recommendEntity: RecommendEntity): Result<BaseResponse> = repository.saveCourse(recommendEntity)
}