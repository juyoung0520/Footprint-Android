package com.footprint.footprint.viewmodel

import androidx.lifecycle.*
import com.footprint.footprint.classes.type.NonNullMutableLiveData
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.CourseInfoDTO
import com.footprint.footprint.data.dto.MonthBadgeInfoDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.domain.usecase.GetCourseInfoUseCase
import com.footprint.footprint.domain.usecase.GetCoursesUseCase
import com.footprint.footprint.domain.usecase.GetSimpleUserUseCase
import com.footprint.footprint.domain.usecase.MarkCourseUseCase
import com.footprint.footprint.ui.main.course.Filtering
import com.footprint.footprint.ui.main.course.Filtering.filterState
import com.footprint.footprint.utils.*
import kotlinx.coroutines.launch

class CourseDetailViewModel(private val getSimpleUserUseCase: GetSimpleUserUseCase, private val getCourseInfoUseCase: GetCourseInfoUseCase, private val markCourseUseCase: MarkCourseUseCase) : BaseViewModel() {
    private var errorMethod: String? = null
    fun getErrorType(): String = this.errorMethod.toString()

    /* 코스 조회 */
    private val _courseInfo: MutableLiveData<CourseInfoDTO> = MutableLiveData()
    val courseInfo: LiveData<CourseInfoDTO> get() = _courseInfo

    fun getCourseInfo(courseIdx: Int){
        viewModelScope.launch {
            when (val response = getCourseInfoUseCase.invoke(courseIdx)) {
                is Result.Success -> {
                    _courseInfo.postValue(response.value)
                }
                is Result.NetworkError -> {
                    errorMethod = "getCourseInfo"

                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "getCourseInfo"

                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }

    /* 코스 북마크 */
    private val _isMarked: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isMarked: SingleLiveEvent<Boolean> get() = _isMarked

    fun markCourse(courseIdx: Int){
        viewModelScope.launch {
            when (val response = markCourseUseCase.invoke(courseIdx)) {
                is Result.Success -> {
                    when(response.value.code){
                        1000 -> {
                            if(response.value.result=="찜하기")
                                _isMarked.postValue(true)
                            else
                                _isMarked.postValue(false)
                        }
                    }
                }
                is Result.NetworkError -> {
                    errorMethod = "markCourse"

                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "markCourse"

                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }

    /* 유저 정보 */
    private val _user: MutableLiveData<SimpleUserModel> = MutableLiveData()
    val user: LiveData<SimpleUserModel> get() = _user

    fun getUser(){
        viewModelScope.launch {
            when(val response = getSimpleUserUseCase.invoke()) {
                is Result.Success -> _user.value = response.value
                is Result.NetworkError -> {
                    errorMethod = "getUser"
                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "getUser"

                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }
}
