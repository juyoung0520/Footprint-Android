package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.CourseRemoteDataSource
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.data.dto.*
import com.footprint.footprint.data.mapper.CourseMapper
import com.footprint.footprint.domain.model.SelfCourseEntity
import com.footprint.footprint.domain.model.RecommendEntity
import com.footprint.footprint.domain.model.WalkDetailCEntity
import com.footprint.footprint.domain.repository.CourseRepository
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class CourseRepositoryImpl(private val dataSource: CourseRemoteDataSource): CourseRepository {
    override suspend fun getCourses(bounds: BoundsModel): Result<List<CourseDTO>> {

        val encryptedData = NetworkUtils.encrypt(bounds)
        LogUtils.d("COURSE/API-DATA(E)", encryptedData)
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        return when(val response = dataSource.getCourses(data)){
            is Result.Success -> {
                if (response.value.isSuccess){
                    Result.Success(NetworkUtils.decrypt(response.value.result, getCourseLists::class.java).getCourseLists)
                }
                else{
                    Result.GenericError(response.value.code, "")
                }
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun markCourse(courseIdx: Int): Result<BaseResponse> {
        return when(val response = dataSource.markCourse(courseIdx)){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(response.value)
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun getCourseInfo(courseIdx: Int): Result<CourseInfoDTO> {
        return when(val response = dataSource.getCourseInfo(courseIdx)) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(
                        NetworkUtils.decrypt(
                            response.value.result,
                            CourseInfoDTO::class.java
                        )
                    )
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

  override suspend fun evaluateCourse(courseIdx: Int, evaluate: Int): Result<BaseResponse> {
        return when(val response = dataSource.evaluateCourse(courseIdx, evaluate)) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    response
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun getMarkedCourses(): Result<List<CourseDTO>> {
        return when(val response = dataSource.getMarkedCourses()) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    val courseList = NetworkUtils.decrypt(response.value.result, CourseListDTO::class.java)
                    Result.Success(courseList.courses)
                }
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun getMyRecommendedCourses(): Result<List<CourseDTO>> {
        return when(val response = dataSource.getMyRecommendedCourses()) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    val courseList = NetworkUtils.decrypt(response.value.result, CourseListDTO::class.java)
                    Result.Success(courseList.courses)
                }
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun saveCourse(course: RecommendEntity): Result<BaseResponse> {
        //RecommendEntity -> RecommendDTO
        val recommendDTO: RecommendDTO = CourseMapper.mapperToRecommendDTO(course)

        //recommendDTO 데이터를 암호화
        val encryptedRecommendDTO = NetworkUtils.encrypt(recommendDTO)
        val requestBody: RequestBody = encryptedRecommendDTO.toRequestBody("application/json".toMediaTypeOrNull())

        return when (val response = dataSource.saveCourse(requestBody)) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(response.value)
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun getWalkDetailC(walkNumber: Int): Result<WalkDetailCEntity> {
        return when (val response = dataSource.getWalkDetailForMakeCourse(walkNumber)) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    //response 데이터를 복호화
                    val walkDetailCDTO: WalkDetailCDTO = NetworkUtils.decrypt(response.value.result, WalkDetailCDTO::class.java)
                    //WalkDetailCDTO -> WalkDetailCEntity 로 매핑
                    Result.Success(CourseMapper.mapperToWalkDetailCEntity(walkDetailCDTO))
                } else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun getSelfCourseList(): Result<List<SelfCourseEntity>> {
        return when (val response = dataSource.getSelfCourseLise()) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    //response 데이터를 복호화
                    val getSelfCourseDTOs: GetSelfCourseDTO = NetworkUtils.decrypt(response.value.result, GetSelfCourseDTO::class.java)
                    //List<SelfCourseDTO> -> List<SelfCourseEntity> 로 매핑
                    Result.Success(CourseMapper.mapperToGetSelfCourseListEntity(getSelfCourseDTOs.getWalks))
                } else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun deleteCourse(courseIdx: Int): Result<BaseResponse> {
        return when (val response = dataSource.deleteCourse(courseIdx)) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    Result.Success(response.value)
                } else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
}