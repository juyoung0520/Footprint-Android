package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.model.Goal
import com.footprint.footprint.domain.usecase.GetThisMonthGoalUseCase
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.LogUtils
import kotlinx.coroutines.launch

class GoalViewModel(private val getThisMonthGoalUseCase: GetThisMonthGoalUseCase): BaseViewModel() {
    private val _thisMonthGoal: MutableLiveData<Goal> = MutableLiveData()
    val thisMonthGoal: LiveData<Goal> get() = _thisMonthGoal

    fun getThisMonthGoal() {
        viewModelScope.launch {
            when (val response = getThisMonthGoalUseCase.invoke()) {
                is Result.Success -> _thisMonthGoal.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}