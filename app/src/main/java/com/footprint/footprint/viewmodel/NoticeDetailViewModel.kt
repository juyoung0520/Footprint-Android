package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.usecase.GetNoticeUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class NoticeDetailViewModel(private val getNoticeUseCase :GetNoticeUseCase): BaseViewModel() {
    private val _notice = SingleLiveEvent<NoticeDto>()
    val notice: LiveData<NoticeDto> get() = _notice

    fun getNotice(idx: Int){
        viewModelScope.launch {
            when(val response = getNoticeUseCase.invoke(idx)){
                is Result.Success -> _notice.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}