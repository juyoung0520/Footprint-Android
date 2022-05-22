package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.UserInfoDTO
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.domain.usecase.GetUserInfoUseCase
import com.footprint.footprint.domain.usecase.GetSimpleUserUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val getSimpleUserUseCase: GetSimpleUserUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : BaseViewModel() {
    private val _userInfo = SingleLiveEvent<SimpleUserModel>()
    val userInfo: LiveData<SimpleUserModel> get() = _userInfo

    private val _infoDetail = SingleLiveEvent<UserInfoDTO>()
    val infoDetail: LiveData<UserInfoDTO> get() = _infoDetail

    fun getSimpleUser() {
        viewModelScope.launch {
            when (val response = getSimpleUserUseCase.invoke()) {
                is Result.Success -> _userInfo.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            when (val response = getUserInfoUseCase()) {
                is Result.Success -> _infoDetail.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}