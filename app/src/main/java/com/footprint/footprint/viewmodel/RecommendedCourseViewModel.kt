package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.usecase.DeleteCourseUseCase
import com.footprint.footprint.domain.usecase.GetMarkedCoursesUseCase
import com.footprint.footprint.domain.usecase.GetMyRecommendedCoursesUseCase
import com.footprint.footprint.domain.usecase.MarkCourseUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class RecommendedCourseViewModel(private val getMarkedCoursesUseCase: GetMarkedCoursesUseCase, private val getMyRecommendedCoursesUseCase: GetMyRecommendedCoursesUseCase, private val deleteCourseUseCase: DeleteCourseUseCase, private val markCourseUseCase: MarkCourseUseCase): BaseViewModel() {
    private var errorMethod: String? = null

    private val _recommendedCourses = SingleLiveEvent<List<CourseDTO>>()
    val recommendedCourses: LiveData<List<CourseDTO>> get() = _recommendedCourses

    private val _deleteResultCode = MutableLiveData<Int>()
    val deleteResultCode: LiveData<Int> get() = _deleteResultCode

    private val _markedCourses = SingleLiveEvent<List<CourseDTO>>()
    val markedCourses: LiveData<List<CourseDTO>> get() = _markedCourses

    private val _markCourseResultCode = MutableLiveData<Int>()
    val markCourseResultCode: LiveData<Int> get() = _markCourseResultCode

    fun getMarkedCourses() {
        errorMethod = "getMarkedCourses"

        viewModelScope.launch {
            when (val response = getMarkedCoursesUseCase()) {
                is Result.Success -> _markedCourses.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getRecommendedCourses() {
        errorMethod = "getRecommendedCourses"

        viewModelScope.launch {
            when(val response = getMyRecommendedCoursesUseCase.invoke()) {
                is Result.Success -> _recommendedCourses.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun deleteCourse(courseIdx: Int) {
        errorMethod = "deleteCourse"

        viewModelScope.launch {
            when(val response = deleteCourseUseCase.invoke(courseIdx)) {
                is Result.Success -> _deleteResultCode.postValue(response.value.code)
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun markCourse(courseIdx: Int) {
        errorMethod = "markCourse"

        viewModelScope.launch {
            when (val response = markCourseUseCase.invoke(courseIdx)) {
                is Result.Success -> _markCourseResultCode.postValue(response.value.code)
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getErrorType(): String = this.errorMethod!!
}