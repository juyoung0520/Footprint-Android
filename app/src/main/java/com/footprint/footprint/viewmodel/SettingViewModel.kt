package com.footprint.footprint.viewmodel

import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.usecase.GetNewNoticeUseCase
import com.footprint.footprint.domain.usecase.UnRegisterUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class SettingViewModel(private val unRegisterUseCase: UnRegisterUseCase, private val getNewNoticeUseCase: GetNewNoticeUseCase): BaseViewModel() {
    private val _isDeleted: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isDeleted: SingleLiveEvent<Boolean> get() = _isDeleted

    private val _isNewNoticeExist: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isNewNoticeExist: SingleLiveEvent<Boolean> get() = _isNewNoticeExist

    fun unRegister(){
        viewModelScope.launch {
            when(val response = unRegisterUseCase.invoke()){
                is Result.Success -> {
                    when(response.value.code){
                        1000 -> _isDeleted.postValue(true)
                        else-> _isDeleted.postValue(false)
                    }
                }
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getNewNotice(){
        viewModelScope.launch {
            when(val response = getNewNoticeUseCase.invoke()){
                is Result.Success -> {
                    _isNewNoticeExist.postValue(response.value.noticeNew)
                }
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}