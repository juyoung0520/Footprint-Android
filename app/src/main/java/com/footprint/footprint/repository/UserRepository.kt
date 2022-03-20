package com.footprint.footprint.repository

import com.footprint.footprint.data.model.SimpleUserModel
import com.footprint.footprint.data.remote.user.UserRegisterResponse
import retrofit2.Response

interface UserRepository {
    fun updateUser(user: SimpleUserModel, onResponse: (Response<UserRegisterResponse>) -> Unit, onFailure: (Throwable) -> Unit)
}