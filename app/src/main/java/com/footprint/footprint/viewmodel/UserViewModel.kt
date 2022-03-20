package com.footprint.footprint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.footprint.footprint.data.model.SimpleUserModel
import com.footprint.footprint.data.remote.user.User
import com.footprint.footprint.repository.UserRepositoryImpl
import com.footprint.footprint.utils.LogUtils

class UserViewModel: ViewModel() {
    private val userRepository: UserRepositoryImpl = UserRepositoryImpl()

    private val _user: MutableLiveData<User> = MutableLiveData<User>()
    private val _isUpdate: MutableLiveData<Boolean> = MutableLiveData()

    val user: LiveData<User> get() = _user
    val isEdit: LiveData<Boolean> get() = _isUpdate

    fun updateUser(user: SimpleUserModel) {
        userRepository.updateUser(
            user,
            onResponse = {
                when(it.body()!!.code){
                    1000 -> _isUpdate.postValue(true)
                    else -> _isUpdate.postValue(false)
                }
            },
            onFailure = {
                LogUtils.e("UserViewModel", "onFailure: $it")
                _isUpdate.postValue(false)
            }
        )
    }
}