package com.footprint.footprint.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.RemoteErrorEmitter

abstract class BaseViewModel : ViewModel() {
    val mutableErrorType = SingleLiveEvent<ErrorType>()
}