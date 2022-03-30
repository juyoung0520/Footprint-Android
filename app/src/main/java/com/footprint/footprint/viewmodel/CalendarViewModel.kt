package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.DayWalkDTO
import com.footprint.footprint.data.dto.MonthDayDTO
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.usecase.GetDayWalksUseCase
import com.footprint.footprint.domain.usecase.GetMonthWalksUseCase
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.LogUtils
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val getMonthWalksUseCase: GetMonthWalksUseCase,
    private val getDayWalksUseCase: GetDayWalksUseCase
) : BaseViewModel() {
    private val _monthDays = SingleLiveEvent<List<MonthDayDTO>>()
    val monthDays: LiveData<List<MonthDayDTO>> get() = _monthDays

    private val _dayWalks = SingleLiveEvent<List<DayWalkDTO>>()
    val dayWalks: LiveData<List<DayWalkDTO>> get() = _dayWalks

    fun getMonthWalks(year: Int, month: Int) {
        viewModelScope.launch {
            when (val response = getMonthWalksUseCase(year, month)) {
                is Result.Success -> _monthDays.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getDayWalks(date: String) {
        viewModelScope.launch {
            when (val response = getDayWalksUseCase(date)) {
                is Result.Success -> _dayWalks.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    override fun onCleared() {
        LogUtils.d("CalendarViewModel", "oncleard")
        super.onCleared()
    }
}