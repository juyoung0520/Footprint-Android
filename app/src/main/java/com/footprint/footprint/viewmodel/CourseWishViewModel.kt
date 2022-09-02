package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.usecase.GetMarkedCoursesUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class CourseWishViewModel(private val getMarkedCoursesUseCase: GetMarkedCoursesUseCase): BaseViewModel() {
    private val _markedCourses = SingleLiveEvent<List<CourseDTO>>()
    val markedCourses: LiveData<List<CourseDTO>> get() = _markedCourses

    fun getMarkedCourses() {
        viewModelScope.launch {
            when(val response = getMarkedCoursesUseCase()) {
                is Result.Success -> _markedCourses.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}