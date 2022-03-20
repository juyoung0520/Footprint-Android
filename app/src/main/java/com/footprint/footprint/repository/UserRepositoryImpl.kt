package com.footprint.footprint.repository

import com.footprint.footprint.data.model.SimpleUserModel
import com.footprint.footprint.data.remote.user.UserRegisterResponse
import com.footprint.footprint.data.remote.user.UserRetrofitInterface
import com.footprint.footprint.utils.GlobalApplication
import com.footprint.footprint.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepositoryImpl: UserRepository {
    private val userService: UserRetrofitInterface = GlobalApplication.retrofit.create(UserRetrofitInterface::class.java)

    override fun updateUser(
        user: SimpleUserModel,
        onResponse: (Response<UserRegisterResponse>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val encryptedData = NetworkUtils.encrypt(user)
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        userService.updateUser(data).enqueue(object : Callback<UserRegisterResponse>{
            override fun onResponse(call: Call<UserRegisterResponse>, response: Response<UserRegisterResponse>) {
                onResponse(response)
            }

            override fun onFailure(call: Call<UserRegisterResponse>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}