package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.usecase.DeleteCourseUseCase
import com.footprint.footprint.domain.usecase.GetMyRecommendedCoursesUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class RecommendedCourseViewModel(private val getMarkedCoursesUseCase: GetMarkedCoursesUseCase, private val getMyRecommendedCoursesUseCase: GetMyRecommendedCoursesUseCase, private val deleteCourseUseCase: DeleteCourseUseCase): BaseViewModel() {
    private val _recommendedCourses = SingleLiveEvent<List<CourseDTO>>()
    val recommendedCourses: LiveData<List<CourseDTO>> get() = _recommendedCourses

    private val _deleteResultCode = MutableLiveData<Int>()
    val deleteResultCode: LiveData<Int> get() = _deleteResultCode

    fun getMarkedCourses() {
        viewModelScope.launch {
            when (val response = getMarkedCoursesUseCase()) {
                is Result.Success -> _markedCourses.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getRecommendedCourses() {
        viewModelScope.launch {
            when(val response = getMyRecommendedCoursesUseCase()) {
                is Result.Success -> _recommendedCourses.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun deleteCourse(courseIdx: Int) {
        viewModelScope.launch {
            when(val response = deleteCourseUseCase(courseIdx)) {
                is Result.Success -> _deleteResultCode.postValue(response.value.code)
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}