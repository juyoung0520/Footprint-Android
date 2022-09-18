package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.WalkDetailCEntity
import com.footprint.footprint.domain.usecase.GetWalkDetailCUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class CourseSetViewModel(private val getWalkDetailCUseCase: GetWalkDetailCUseCase): BaseViewModel() {
    private val _walkDetailCEntity: MutableLiveData<WalkDetailCEntity> = MutableLiveData()
    val walkDetailCEntity: LiveData<WalkDetailCEntity> get() = _walkDetailCEntity

    fun getWalkDetailCEntity(walkNumber: Int) {
        viewModelScope.launch {
            when (val response = getWalkDetailCUseCase.invoke(walkNumber)) {
                is Result.Success -> _walkDetailCEntity.value = response.value
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