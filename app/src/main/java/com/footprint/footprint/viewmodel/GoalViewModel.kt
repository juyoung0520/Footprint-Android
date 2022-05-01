package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Goal
import com.footprint.footprint.domain.model.UpdateGoal
import com.footprint.footprint.domain.usecase.GetNextMonthGoalUseCase
import com.footprint.footprint.domain.usecase.GetThisMonthGoalUseCase
import com.footprint.footprint.domain.usecase.UpdateGoalUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class GoalViewModel(private val getThisMonthGoalUseCase: GetThisMonthGoalUseCase, private val getNextMonthGoalUseCase: GetNextMonthGoalUseCase, private val updateGoalUseCase: UpdateGoalUseCase): BaseViewModel() {
    private val _thisMonthGoal: MutableLiveData<Goal> = MutableLiveData()
    val thisMonthGoal: LiveData<Goal> get() = _thisMonthGoal

    private val _nextMonthGoal: MutableLiveData<Goal> = MutableLiveData()
    val nextMonthGoal: LiveData<Goal> get() = _nextMonthGoal

    fun getThisMonthGoal() {
        viewModelScope.launch {
            when (val response = getThisMonthGoalUseCase.invoke()) {
                is Result.Success -> _thisMonthGoal.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getNextMonthGoal() {
        viewModelScope.launch {
            when (val response = getNextMonthGoalUseCase.invoke()) {
                is Result.Success -> _nextMonthGoal.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun updateGoal(month: String, goal: UpdateGoal) {
        viewModelScope.launch {
            when (val response = updateGoalUseCase.invoke(month, goal)) {
                is Result.Success -> _nextMonthGoal.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}