package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.data.dto.Login
import com.footprint.footprint.domain.usecase.AutoLoginUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class SplashViewModel(private val autoLoginUseCase: AutoLoginUseCase): BaseViewModel() {
    private val _thisLogin: MutableLiveData<Login> = MutableLiveData()
    val thisLogin: LiveData<Login> get() = _thisLogin

    fun autoLogin(){
        viewModelScope.launch {
            when(val response = autoLoginUseCase.invoke()) {
                is Result.Success -> _thisLogin.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    when(response.code){
                        2001, 2002, 2003, 2004 -> mutableErrorType.postValue(ErrorType.JWT)
                        else -> mutableErrorType.postValue(ErrorType.UNKNOWN)
                    }
                }
            }
        }
    }
}