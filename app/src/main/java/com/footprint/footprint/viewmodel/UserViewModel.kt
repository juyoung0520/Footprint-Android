package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.User
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.domain.model.UserModel
import com.footprint.footprint.domain.usecase.GetUserUseCase
import com.footprint.footprint.domain.usecase.RegisterUserUseCase
import com.footprint.footprint.domain.usecase.UpdateUserUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class UserViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    ): BaseViewModel() {

    private val _thisUser: MutableLiveData<User> = MutableLiveData()
    val thisUser: LiveData<User> get() = _thisUser

    private val _isRegistered: MutableLiveData<Boolean> = MutableLiveData()
    val isRegistered: LiveData<Boolean> get() = _isRegistered

    private val _isUpdate: MutableLiveData<Boolean> = MutableLiveData()
    val isUpdate: LiveData<Boolean> get() = _isUpdate

    fun registerUser(userModel: UserModel){
        viewModelScope.launch {
            when(val response = registerUserUseCase.invoke(userModel)){
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

    fun updateUser(simpleUserModel: SimpleUserModel){
        viewModelScope.launch {
            when(val response = updateUserUseCase.invoke(simpleUserModel)){
                is Result.Success -> {
                    when(response.value.code){
                        1000 -> _isUpdate.postValue(true)
                        else-> _isUpdate.postValue(false)
                    }
                }
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getUser(){
        viewModelScope.launch {
            when(val response = getUserUseCase.invoke()) {
                is Result.Success -> _thisUser.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}