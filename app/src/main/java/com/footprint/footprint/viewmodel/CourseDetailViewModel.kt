package com.footprint.footprint.viewmodel

import androidx.lifecycle.*
import com.footprint.footprint.classes.type.NonNullMutableLiveData
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.CourseInfoDTO
import com.footprint.footprint.data.dto.MonthBadgeInfoDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.domain.usecase.GetCourseInfoUseCase
import com.footprint.footprint.domain.usecase.GetCoursesUseCase
import com.footprint.footprint.domain.usecase.MarkCourseUseCase
import com.footprint.footprint.ui.main.course.Filtering
import com.footprint.footprint.ui.main.course.Filtering.filterState
import com.footprint.footprint.utils.*
import kotlinx.coroutines.launch

class CourseDetailViewModel(private val getCourseInfoUseCase: GetCourseInfoUseCase) : BaseViewModel() {
    private var errorMethod: String? = null

    private val _courseInfo: MutableLiveData<CourseInfoDTO> = MutableLiveData()
    val courseInfo: LiveData<CourseInfoDTO> get() = _courseInfo

    fun getCourseInfo(courseIdx: Int){

        viewModelScope.launch {
            when (val response = getCourseInfoUseCase.invoke(courseIdx)) {
                is Result.Success -> {
                    _courseInfo.postValue(response.value)
                }
                is Result.NetworkError -> {
                    errorMethod = "getCourses"

                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "getCourses"

                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }
}
