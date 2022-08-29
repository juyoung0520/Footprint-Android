package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.domain.usecase.GetAddressUseCase
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class MapViewModel(private val getAddressUseCase: GetAddressUseCase): BaseViewModel() {
    private var errorMethod: String? = null

    private val _address: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> get() = _address

    fun getAddress(coords: String) {
        errorMethod = "getAddress"

        viewModelScope.launch {
            when (val result = getAddressUseCase.invoke(coords)) {
                is Result.Success -> _address.postValue("${result.value.area1} ${result.value.area2}")
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.ADDRESS)
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
            }
        }
    }

    fun getErrorType(): String = this.errorMethod!!
}