package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.usecase.EvaluateCourseUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class CourseWalkViewModel(private val evaluateCourseUseCase: EvaluateCourseUseCase): BaseViewModel() {
    private val _isReviewed = SingleLiveEvent<Boolean>()
    val isReviewed: LiveData<Boolean> get() = _isReviewed

    fun evaluateCourse(courseIdx: Int, evaluate: Int) {
        viewModelScope.launch {
            when(val response = evaluateCourseUseCase(courseIdx, evaluate)) {
                is Result.Success -> _isReviewed.value = true
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}