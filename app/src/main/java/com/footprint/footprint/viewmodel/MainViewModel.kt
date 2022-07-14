package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.*
import com.footprint.footprint.domain.usecase.GetKeyNoticeUseCase
import com.footprint.footprint.domain.usecase.GetMonthBadgeUseCase
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.LogUtils
import kotlinx.coroutines.launch

class MainViewModel(private val getMonthBadgeUseCase: GetMonthBadgeUseCase, private val getKeyNoticeUseCase: GetKeyNoticeUseCase): BaseViewModel() {
    private var errorMethod: String? = null

    private val _thisMonthBadge: MutableLiveData<MonthBadgeInfoDTO> = MutableLiveData()
    val thisMonthBadge: LiveData<MonthBadgeInfoDTO> get() = _thisMonthBadge

    private val _thisKeyNoticeList: MutableLiveData<KeyNoticeDto> = MutableLiveData()
    val thisKeyNoticeList: LiveData<KeyNoticeDto> get() = _thisKeyNoticeList

    fun getMonthBadge(){
        viewModelScope.launch {
            when(val response = getMonthBadgeUseCase.invoke()){
                is Result.Success -> _thisMonthBadge.value = response.value
                is Result.NetworkError -> {
                    errorMethod = "getMonthBadge"
                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "getMonthBadge"

                    when(response.code){
                        3030, 2118 -> mutableErrorType.postValue(ErrorType.NO_BADGE) // 이번 달에 획득한 뱃지가 없습니다. 이전 달 설정한 목표가 없습니다.
                        600 -> mutableErrorType.postValue(ErrorType.UNKNOWN)
                        else -> mutableErrorType.postValue(ErrorType.DB_SERVER)
                    }
                }
            }
        }
    }

    fun getKeyNotice(){
        viewModelScope.launch {
            when(val response = getKeyNoticeUseCase.invoke()){
                is Result.Success -> _thisKeyNoticeList.value = response.value
                is Result.NetworkError -> {
                    errorMethod = "getKeyNotice"
                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "getKeyNotice"

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