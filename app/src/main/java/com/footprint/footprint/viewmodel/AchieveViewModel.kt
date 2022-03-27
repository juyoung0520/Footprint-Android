package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.AchieveDetailResult
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.usecase.GetInfoDetailUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class AchieveViewModel(private val getInfoDetailUseCase: GetInfoDetailUseCase): BaseViewModel() {
    private val _infoDetail: MutableLiveData<AchieveDetailResult> = MutableLiveData()
    val infoDetail: LiveData<AchieveDetailResult> get() = _infoDetail

    fun getInfoDetail() {
        viewModelScope.launch {
            when(val response = getInfoDetailUseCase()) {
                is Result.Success -> _infoDetail.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}