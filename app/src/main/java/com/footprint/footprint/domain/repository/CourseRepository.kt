package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.*
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.SelfCourseEntity
import com.footprint.footprint.domain.model.RecommendEntity
import com.footprint.footprint.domain.model.WalkDetailCEntity

interface CourseRepository {
    suspend fun getCourses(bounds: BoundsModel): Result<List<CourseDTO>>
    suspend fun markCourse(courseIdx: Int) : Result<BaseResponse>
    suspend fun getCourseInfo(courseIdx: Int) : Result<CourseInfoDTO>
    suspend fun evaluateCourse(courseIdx: Int, evaluate: Int): Result<BaseResponse>
    suspend fun getMarkedCourses(): Result<List<CourseDTO>>
    suspend fun getMyRecommendedCourses(): Result<List<CourseDTO>>
    suspend fun saveCourse(course: RecommendEntity): Result<BaseResponse>
    suspend fun getWalkDetailC(walkNumber: Int): Result<WalkDetailCEntity>
    suspend fun getSelfCourseList(): Result<List<SelfCourseEntity>>
    suspend fun deleteCourse(courseIdx: Int): Result<BaseResponse>
}