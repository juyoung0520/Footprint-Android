package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.SimpleUser
import com.footprint.footprint.domain.usecase.UpdateUserUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class UserViewModel(private val updateUserUseCase: UpdateUserUseCase): BaseViewModel() {
    private val _user: MutableLiveData<SimpleUser> = MutableLiveData()
    val user: LiveData<SimpleUser> get() = _user

    fun updateUser(user: SimpleUser) {
        viewModelScope.launch {
            when (val response = updateUserUseCase.invoke(user)) {
                is Result.Success -> _user.postValue(response.value)
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}