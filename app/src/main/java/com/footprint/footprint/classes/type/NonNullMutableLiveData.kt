package com.footprint.footprint.classes.type

import androidx.lifecycle.MutableLiveData

class NonNullMutableLiveData<T: Any>(defaultValue: T): MutableLiveData<T>() {
    init {
        value = defaultValue
    }

    override fun getValue() = super.getValue()!!
}