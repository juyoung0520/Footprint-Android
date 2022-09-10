package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.UpdateCourseReqEntity
import com.footprint.footprint.domain.repository.CourseRepository

class UpdateCourseUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(updateCourseReqEntity: UpdateCourseReqEntity): Result<BaseResponse> = repository.updateCourse(updateCourseReqEntity)
}