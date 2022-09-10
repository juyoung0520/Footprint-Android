package com.footprint.footprint.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.GetCourseByCourseNameEntity
import com.footprint.footprint.domain.model.RecommendEntity
import com.footprint.footprint.domain.model.UpdateCourseReqEntity
import com.footprint.footprint.domain.usecase.GetAddressUseCase
import com.footprint.footprint.domain.usecase.GetCourseByCourseNameUseCase
import com.footprint.footprint.domain.usecase.SaveCourseUseCase
import com.footprint.footprint.domain.usecase.UpdateCourseUseCase
import com.footprint.footprint.service.S3UploadService
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.getAbsolutePathByBitmap
import com.footprint.footprint.utils.uriToBitmap
import kotlinx.coroutines.launch
import java.io.File

class CourseShareViewModel(private val saveCourseUseCase: SaveCourseUseCase, private val getAddressUseCase: GetAddressUseCase, private val getCourseByCourseNameUseCase: GetCourseByCourseNameUseCase, private val updateCourseUseCase: UpdateCourseUseCase): BaseViewModel() {
    private var errorMethod: String? = null

    private val _saveCourseRes: MutableLiveData<BaseResponse> = MutableLiveData()
    val saveCourseRes: LiveData<BaseResponse> get() = _saveCourseRes

    private val _address: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> get() = _address

    private val _course: MutableLiveData<GetCourseByCourseNameEntity> = MutableLiveData()
    val course: LiveData<GetCourseByCourseNameEntity> get() = _course

    private val _updateCourseRes: MutableLiveData<BaseResponse> = MutableLiveData()
    val updateCourseRes: LiveData<BaseResponse> get() = _updateCourseRes

    private fun callSaveCourseAPI(recommendEntity: RecommendEntity) {
        viewModelScope.launch {
            when (val response = saveCourseUseCase.invoke(recommendEntity)) {
                is Result.Success -> _saveCourseRes.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    when (response.code) {
                        2151 -> _saveCourseRes.value = BaseResponse(false, response.code, response.error, null) //코스 중복 이름 에러
                        600 -> mutableErrorType.postValue(ErrorType.UNKNOWN) //Retrofit 에러
                        else -> mutableErrorType.postValue(ErrorType.DB_SERVER)   //SERVER 에러
                    }
                }
            }
        }
    }

    private fun callUpdateCourseAPI(updateCourseReqEntity: UpdateCourseReqEntity) {
        viewModelScope.launch {
            when (val response = updateCourseUseCase.invoke(updateCourseReqEntity)) {
                is Result.Success -> _saveCourseRes.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    when (response.code) {
                        2151 -> _saveCourseRes.value = BaseResponse(false, response.code, response.error, null) //코스 중복 이름 에러
                        600 -> mutableErrorType.postValue(ErrorType.UNKNOWN) //Retrofit 에러
                        else -> mutableErrorType.postValue(ErrorType.DB_SERVER)   //SERVER 에러
                    }
                }
            }
        }
    }

    fun saveCourse(context: Context, recommendEntity: RecommendEntity) {
        errorMethod = "saveCourse"

        if (recommendEntity.courseImg.isNotBlank()) {  //이미지가 있으면 S3에 저장해서 URL 받아오기
            val path: String = getAbsolutePathByBitmap(context, uriToBitmap(context, Uri.parse(recommendEntity.courseImg)))
            S3UploadService.uploadImg(context, File(path))  //산책 이미지 저장
        } else {
            callSaveCourseAPI(recommendEntity)
        }

        S3UploadService.setCallback(object : S3UploadService.Callback {
            override fun successUploadImg(img: String) {
                recommendEntity.courseImg = img
                callSaveCourseAPI(recommendEntity)
            }

            override fun failUploadImg() {
                mutableErrorType.postValue(ErrorType.S3) //Retrofit 에러
            }

            override fun successFootprintImg(img: String, footprintIdx: Int, imgIdx: Int) {
            }

            override fun failFootprintImg(footprintIdx: Int, imgIdx: Int) {
            }
        })
    }

    fun getAddress(coords: String) {
        errorMethod = "getAddress"

        viewModelScope.launch {
            when (val result = getAddressUseCase.invoke(coords)) {
                is Result.Success -> _address.postValue("${result.value.area1} ${result.value.area2}")
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.ADDRESS)
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
            }
        }
    }

    fun getCourse(courseName: String) {
        errorMethod = "getCourse"

        viewModelScope.launch {
            when (val result = getCourseByCourseNameUseCase.invoke(courseName)) {
                is Result.Success -> _course.postValue(result.value)
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    when (result.code) {
                        600 -> mutableErrorType.postValue(ErrorType.UNKNOWN) //Retrofit 에러
                        else -> mutableErrorType.postValue(ErrorType.DB_SERVER)   //SERVER 에러
                    }
                }
            }
        }
    }

    fun updateCourse(context: Context, updateCourseReqEntity: UpdateCourseReqEntity) {
        errorMethod = "updateCourse"

        if (updateCourseReqEntity.courseImg.isNotBlank()) {  //이미지가 있으면 S3에 저장해서 URL 받아오기
            val path: String = getAbsolutePathByBitmap(context, uriToBitmap(context, Uri.parse(updateCourseReqEntity.courseImg)))
            S3UploadService.uploadImg(context, File(path))  //산책 이미지 저장
        } else {
            callUpdateCourseAPI(updateCourseReqEntity)
        }

        S3UploadService.setCallback(object : S3UploadService.Callback {
            override fun successUploadImg(img: String) {
                updateCourseReqEntity.courseImg = img
                callUpdateCourseAPI(updateCourseReqEntity)
            }

            override fun failUploadImg() {
                mutableErrorType.postValue(ErrorType.S3) //Retrofit 에러
            }

            override fun successFootprintImg(img: String, footprintIdx: Int, imgIdx: Int) {
            }

            override fun failFootprintImg(footprintIdx: Int, imgIdx: Int) {
            }
        })
    }

    fun getErrorType(): String = this.errorMethod!!
}