package com.footprint.footprint.retrofit

import com.footprint.footprint.data.remote.user.UserRegisterResponse
import com.footprint.footprint.data.remote.user.UserResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserService {
    //초기 정보 등록 API
    @POST("users/infos")
    fun registerUser(@Body userModel: RequestBody): Call<UserRegisterResponse>

    //유저 정보 조회 API
    @GET("users")
    fun getUser() : Call<UserResponse>

    //유저 정보 수정 API
    @PATCH("users/infos/after")
    fun updateUser(@Body simpleUserModel: RequestBody) : Call<UserRegisterResponse>
}