package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Badge
import com.footprint.footprint.domain.model.BadgeInfo
import com.footprint.footprint.domain.model.RepresentativeBadge
import com.footprint.footprint.domain.usecase.ChangeRepresentativeBadgeUseCase
import com.footprint.footprint.domain.usecase.GetBadgesUseCase
import com.footprint.footprint.utils.ErrorType
import kotlinx.coroutines.launch

class BadgeViewModel(private val getBadgesUseCase: GetBadgesUseCase, private val changeRepresentativeBadgeUseCase: ChangeRepresentativeBadgeUseCase): BaseViewModel() {
    private var errorMethod: String? = null

    private val _badges: MutableLiveData<BadgeInfo> = MutableLiveData()
    val badges: LiveData<BadgeInfo> get() = _badges

    private val _representativeBadge: MutableLiveData<RepresentativeBadge> = MutableLiveData()
    val representativeBadge: LiveData<RepresentativeBadge> get() = _representativeBadge

    fun getBadges() {
        viewModelScope.launch {
            when (val response = getBadgesUseCase.invoke()) {
                is Result.Success -> _badges.postValue(response.value)
                is Result.NetworkError -> {
                    errorMethod = "getBadges"

                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "getBadges"

                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }

    fun changeRepresentativeBadge(badgeIdx: Int) {
        viewModelScope.launch {
            when (val response = changeRepresentativeBadgeUseCase.invoke(badgeIdx)) {
                is Result.Success -> _representativeBadge.postValue(response.value)
                is Result.NetworkError -> {
                    errorMethod = "changeRepresentativeBadge"
                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "changeRepresentativeBadge"
                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }

    fun getErrorMethod(): String = errorMethod!!
}