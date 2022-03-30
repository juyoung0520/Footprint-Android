package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Weather
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.data.dto.TMonth
import com.footprint.footprint.data.dto.Today
import com.footprint.footprint.domain.model.LocationModel
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.domain.usecase.GetTmonthUseCase
import com.footprint.footprint.domain.usecase.GetTodayUseCase
import com.footprint.footprint.domain.usecase.GetSimpleUserUseCase
import com.footprint.footprint.domain.usecase.GetWeatherUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getSimpleUserUseCase: GetSimpleUserUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getTodayUseCase: GetTodayUseCase,
    private val getTmonthUseCase: GetTmonthUseCase
    ): BaseViewModel() {

    private val _thisUser: MutableLiveData<SimpleUserModel> = MutableLiveData()
    val thisUser: LiveData<SimpleUserModel> get() = _thisUser

    private val _thisWeather: MutableLiveData<Weather> = MutableLiveData()
    val thisWeather: LiveData<Weather> get() = _thisWeather

    private val _thisToday: MutableLiveData<Today> = MutableLiveData()
    val thisToday: LiveData<Today> get() = _thisToday

    private val _thisTmonth: MutableLiveData<TMonth> = MutableLiveData()
    val thisTmonth: LiveData<TMonth> get() = _thisTmonth

    fun getUser(){
        viewModelScope.launch {
            when(val response = getSimpleUserUseCase.invoke()) {
                is Result.Success -> _thisUser.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getWeather(location: LocationModel){
        viewModelScope.launch {
            when(val response = getWeatherUseCase.invoke(location)){
                is Result.Success -> _thisWeather.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

    fun getToday(){
        viewModelScope.launch {
            when(val response = getTodayUseCase.invoke()){
                is Result.Success -> _thisToday.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
    fun getTmonth(){
        viewModelScope.launch {
            when(val response = getTmonthUseCase.invoke()){
                is Result.Success -> _thisTmonth.value = response.value
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }

}