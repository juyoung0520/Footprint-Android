package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Badge
import com.footprint.footprint.domain.model.Footprint
import com.footprint.footprint.domain.model.Walk
import com.footprint.footprint.domain.usecase.*
import com.footprint.footprint.ui.walk.model.WalkUIModel
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class WalkViewModel(private val getWalkByIdxUseCase: GetWalkByIdxUseCase, private val getFootprintsByWalkIdxUseCase: GetFootprintsByWalkIdxUseCase, private val updateFootprintUseCase: UpdateFootprintUseCase, private val deleteWalkUseCase: DeleteWalkUseCase, private val writeWalkUseCase: WriteWalkUseCase): BaseViewModel() {
    private var errorMethod: String? = null

    private val _walk: MutableLiveData<Walk> = MutableLiveData()
    val walk: LiveData<Walk> get() = _walk

    private val _footprints: MutableLiveData<List<Footprint>> = MutableLiveData()
    val footprints: LiveData<List<Footprint>> get() = _footprints

    private val _isUpdate: MutableLiveData<Boolean> = MutableLiveData()
    val isUpdate: LiveData<Boolean> get() = _isUpdate

    private val _isDelete: MutableLiveData<Boolean> = MutableLiveData()
    val isDelete: LiveData<Boolean> get() = _isDelete

    private val _badges: MutableLiveData<List<Badge>> = MutableLiveData()
    val badges: LiveData<List<Badge>> get() = _badges

    fun getWalkByIdx(walkIdx: Int) {
        viewModelScope.launch {
            when (val response = getWalkByIdxUseCase(walkIdx)) {
                is Result.Success -> _walk.postValue(response.value)
                is Result.GenericError -> {
                    errorMethod = "getWalkByIdx"
                    mutableErrorType.postValue(ErrorType.UNKNOWN)
                }
                is Result.NetworkError -> {
                    errorMethod = "getWalkByIdx"
                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
            }
        }
    }

    fun getFootprintsByWalkIdx(walkIdx: Int) {
        viewModelScope.launch {
            when (val response = getFootprintsByWalkIdxUseCase(walkIdx)) {
                is Result.Success -> _footprints.postValue(response.value)
                is Result.GenericError -> {
                    errorMethod = "getFootprintsByWalkIdx"
                    mutableErrorType.postValue(ErrorType.UNKNOWN)
                }
                is Result.NetworkError -> {
                    errorMethod = "getFootprintsByWalkIdx"
                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
            }
        }
    }

    fun getErrorType(): String = this.errorMethod!!

    fun updateFootprint(walkIdx: Int, footprintIdx: Int, footprintMap: HashMap<String, Any>, footprintPhoto: List<String>?) {
        viewModelScope.launch {
            when (updateFootprintUseCase(walkIdx, footprintIdx, footprintMap, footprintPhoto)) {
                is Result.Success -> _isUpdate.postValue(true)
                is Result.GenericError -> {
                    errorMethod = "updateFootprint"
                    mutableErrorType.postValue(ErrorType.UNKNOWN)
                }
                is Result.NetworkError -> {
                    errorMethod = "updateFootprint"
                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
            }
        }
    }

    fun deleteWalk(walkIdx: Int) {
        viewModelScope.launch {
            when (deleteWalkUseCase(walkIdx)) {
                is Result.Success -> _isDelete.postValue((true))
                is Result.GenericError -> {
                    errorMethod = "deleteWalk"
                    mutableErrorType.postValue(ErrorType.UNKNOWN)
                }
                is Result.NetworkError -> {
                    errorMethod = "deleteWalk"
                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
            }
        }
    }

    fun writeWalk(walk: WalkUIModel) {
        viewModelScope.launch {
            when (val response = writeWalkUseCase(walk)) {
                is Result.Success -> _badges.postValue(response.value)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
            }
        }
    }
}