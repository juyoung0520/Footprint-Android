package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.SelfCourseEntity
import com.footprint.footprint.domain.usecase.GetSelfCourseListUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class CourseSelectViewModel(private val getSelfCourseListUseCase: GetSelfCourseListUseCase): BaseViewModel() {
    private val _selfCourseEntities: MutableLiveData<List<SelfCourseEntity>> = MutableLiveData()
    val selfCourseEntities: LiveData<List<SelfCourseEntity>> get() = _selfCourseEntities

    fun getSelfCourses() {
        viewModelScope.launch {
            when (val response = getSelfCourseListUseCase.invoke()) {
                is Result.Success -> _selfCourseEntities.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    if (response.code==600) //Retrofit 에러
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else    //SERVER 에러
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }
}