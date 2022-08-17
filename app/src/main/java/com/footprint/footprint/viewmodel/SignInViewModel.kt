package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.LoginDTO
import com.footprint.footprint.domain.model.SocialUserModel
import com.footprint.footprint.domain.usecase.LoginUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class SignInViewModel (private val loginUseCase: LoginUseCase): BaseViewModel(){
    private val _thisLogin: MutableLiveData<LoginDTO> = MutableLiveData()
    val thisLogin: LiveData<LoginDTO> get() = _thisLogin

    fun login(socialUserModel: SocialUserModel){
        viewModelScope.launch {
            when(val response = loginUseCase.invoke(socialUserModel)) {
                is Result.Success -> _thisLogin.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }
}