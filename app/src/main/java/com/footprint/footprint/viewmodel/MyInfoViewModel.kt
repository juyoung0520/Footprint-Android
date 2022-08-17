package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.MyInfoUserModel
import com.footprint.footprint.domain.usecase.GetMyInfoUserUseCase
import com.footprint.footprint.domain.usecase.UpdateUserUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class MyInfoViewModel(private val getMyInfoUserUseCase: GetMyInfoUserUseCase, private val updateUserUseCase: UpdateUserUseCase): BaseViewModel() {
    private val _thisUser: MutableLiveData<MyInfoUserModel> = MutableLiveData()
    val thisUser: LiveData<MyInfoUserModel> get() = _thisUser

    private val _isUpdate: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isUpdate: SingleLiveEvent<Boolean> get() = _isUpdate

    fun updateUser(myInfoUserModel: MyInfoUserModel){
        viewModelScope.launch {
            when(val response = updateUserUseCase.invoke(myInfoUserModel)){
                is Result.Success -> {
                    when(response.value.code){
                        1000 -> _isUpdate.postValue(true)
                        else-> _isUpdate.postValue(false)
                    }
                }
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    if(response.code == 600 ){
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    }else{
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                    }
                }
            }
        }
    }

    fun getMyInfoUser(){
        viewModelScope.launch {
            when(val response = getMyInfoUserUseCase.invoke()) {
                is Result.Success -> _thisUser.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    if(response.code == 600 ){
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    }else{
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                    }
                }
            }
        }
    }
}