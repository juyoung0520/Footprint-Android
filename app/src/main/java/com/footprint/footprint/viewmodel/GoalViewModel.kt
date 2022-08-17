package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.GoalEntity
import com.footprint.footprint.domain.model.UpdateGoalEntity
import com.footprint.footprint.domain.usecase.GetNextMonthGoalUseCase
import com.footprint.footprint.domain.usecase.GetThisMonthGoalUseCase
import com.footprint.footprint.domain.usecase.UpdateGoalUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class GoalViewModel(private val getThisMonthGoalUseCase: GetThisMonthGoalUseCase, private val getNextMonthGoalUseCase: GetNextMonthGoalUseCase, private val updateGoalUseCase: UpdateGoalUseCase): BaseViewModel() {
    private val _thisMonthGoal: MutableLiveData<GoalEntity> = MutableLiveData()
    val thisMonthGoal: LiveData<GoalEntity> get() = _thisMonthGoal

    private val _nextMonthGoal: MutableLiveData<GoalEntity> = MutableLiveData()
    val nextMonthGoal: LiveData<GoalEntity> get() = _nextMonthGoal

    fun getThisMonthGoal() {
        viewModelScope.launch {
            when (val response = getThisMonthGoalUseCase.invoke()) {
                is Result.Success -> _thisMonthGoal.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    if (response.code==600) //Retrofit 에러
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else    //SERVER 에러
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }

    fun getNextMonthGoal() {
        viewModelScope.launch {
            when (val response = getNextMonthGoalUseCase.invoke()) {
                is Result.Success -> _nextMonthGoal.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    if (response.code==600) //Retrofit 에러
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else    //SERVER 에러
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }

    fun updateGoal(month: String, goalEntity: UpdateGoalEntity) {
        viewModelScope.launch {
            when (val response = updateGoalUseCase.invoke(month, goalEntity)) {
                is Result.Success -> _nextMonthGoal.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> {
                    if (response.code==600) //Retrofit 에러
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else    //SERVER 에러
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }
}