package com.footprint.footprint.data.remote.user

import okhttp3.RequestBody
import retrofit2.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserRetrofitInterface  {
    //초기 정보 등록 API
    @POST("users/infos")
    fun registerUser(@Body userModel: RequestBody): Call<UserRegisterResponse>

    //유저 정보 조회 API
    @GET("users")
    fun getUser() : Call<UserResponse>
}