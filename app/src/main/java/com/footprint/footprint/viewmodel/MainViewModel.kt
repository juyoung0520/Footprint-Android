package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.KeyNoticeDto
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.MonthBadgeResponse
import com.footprint.footprint.domain.usecase.GetKeyNoticeUseCase
import com.footprint.footprint.domain.usecase.GetMonthBadgeUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class MainViewModel(private val getMonthBadgeUseCase: GetMonthBadgeUseCase, private val getKeyNoticeUseCase: GetKeyNoticeUseCase): BaseViewModel() {
    private val _thisMonthBadge: MutableLiveData<MonthBadgeResponse> = MutableLiveData()
    val thisMonthBadge: LiveData<MonthBadgeResponse> get() = _thisMonthBadge

    private val _thisKeyNoticeList: MutableLiveData<KeyNoticeDto> = MutableLiveData()
    val thisKeyNoticeList: LiveData<KeyNoticeDto> get() = _thisKeyNoticeList

    fun getMonthBange(){
        viewModelScope.launch {
            when(val response = getMonthBadgeUseCase.invoke()){
                is Result.Success -> _thisMonthBadge.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getKeyNotice(){
        viewModelScope.launch {
            when(val response = getKeyNoticeUseCase.invoke()){
                is Result.Success -> _thisKeyNoticeList.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}