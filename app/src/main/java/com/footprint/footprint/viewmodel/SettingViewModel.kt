package com.footprint.footprint.viewmodel

import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.usecase.GetNewNoticeUseCase
import com.footprint.footprint.domain.usecase.UnRegisterUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class SettingViewModel(private val unRegisterUseCase: UnRegisterUseCase, private val getNewNoticeUseCase: GetNewNoticeUseCase): BaseViewModel() {
    private var errorMethod: String? = null

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
                is Result.NetworkError -> {
                    errorMethod = "unRegister"
                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "unRegister"
                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }

    fun getNewNotice(){
        viewModelScope.launch {
            when(val response = getNewNoticeUseCase.invoke()){
                is Result.Success -> {
                    _isNewNoticeExist.postValue(response.value.noticeNew)
                }
                is Result.NetworkError -> {
                    errorMethod = "getNewNotice"
                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "getNewNotice"
                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }

    fun getErrorType(): String = this.errorMethod.toString()
}