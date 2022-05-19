package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.NoticeInfoDto
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.usecase.GetNoticeListUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class NoticeListViewModel(
    private val getNoticeListUseCase: GetNoticeListUseCase
): BaseViewModel(){
    private val _noticeList = SingleLiveEvent<Array<NoticeInfoDto>>()
    val noticeList: LiveData<Array<NoticeInfoDto>> get() = _noticeList

    private val _totalPage = SingleLiveEvent<Int>()
    val totalPage: LiveData<Int> get() = _totalPage

    private val _currentPage = SingleLiveEvent<Int>()
    val currentPage: LiveData<Int> get() = _currentPage

    fun getNoticeList(page: Int, size: Int){
        viewModelScope.launch {
            when(val response = getNoticeListUseCase.invoke(page, size)){
                is Result.Success -> {
                    _noticeList.value = response.value.noticeList
                    _totalPage.value = response.value.pageTotal
                    _currentPage.value = response.value.pageOn
                }
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}