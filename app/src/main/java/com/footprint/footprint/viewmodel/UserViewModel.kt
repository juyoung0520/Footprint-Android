package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.model.InitUserModel
import com.footprint.footprint.domain.usecase.RegisterUserUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class UserViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    ): BaseViewModel() {

    private val _isRegistered: MutableLiveData<Boolean> = MutableLiveData()
    val isRegistered: LiveData<Boolean> get() = _isRegistered

    fun registerUser(initUserModel: InitUserModel){
        viewModelScope.launch {
            when(val response = registerUserUseCase.invoke(initUserModel)){
                is Result.Success -> {
                    when(response.value.code){
                        1000 -> _isRegistered.postValue(true)
                        else-> _isRegistered.postValue(false)
                    }
                }
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}