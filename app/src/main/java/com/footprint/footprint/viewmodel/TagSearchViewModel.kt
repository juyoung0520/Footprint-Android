package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.TagWalksDTO
import com.footprint.footprint.domain.usecase.GetTagWalksUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class TagSearchViewModel(private val getTagWalksUseCase: GetTagWalksUseCase) : BaseViewModel() {
    private val _tagWalks = SingleLiveEvent<List<TagWalksDTO>>()
    val tagWalks: LiveData<List<TagWalksDTO>> get() = _tagWalks

    fun getTagWalks(tag: String) {
        viewModelScope.launch {
            when (val response = getTagWalksUseCase(tag)) {
                is Result.Success -> _tagWalks.value = response.value!!
                is Result.NetworkError -> mutableErrorType.postValue(ErrorType.NETWORK)
                is Result.GenericError -> mutableErrorType.postValue(ErrorType.UNKNOWN)
            }
        }
    }
}