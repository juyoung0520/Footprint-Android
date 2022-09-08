package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.usecase.GetMarkedCoursesUseCase
import com.footprint.footprint.domain.usecase.GetMyRecommendedCoursesUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class MyViewModel(
    private val getMarkedCoursesUseCase: GetMarkedCoursesUseCase,
    private val getMyRecommendedCoursesUseCase: GetMyRecommendedCoursesUseCase
) : BaseViewModel() {
    private val _markedCourses = SingleLiveEvent<List<CourseDTO>>()
    val markedCourses: LiveData<List<CourseDTO>> get() = _markedCourses

    private val _recommendedCourses = SingleLiveEvent<List<CourseDTO>>()
    val recommendedCourses: LiveData<List<CourseDTO>> get() = _recommendedCourses

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
}