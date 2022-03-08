package com.footprint.footprint.data.remote.user

import com.footprint.footprint.data.model.SimpleUserModel
import com.footprint.footprint.data.model.UserModel
import retrofit2.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserRetrofitInterface  {
    //초기 정보 등록 API
    @POST("users/infos")
    fun registerUser(@Body userModel: String): Call<UserRegisterResponse>

    //유저 정보 조회 API
    @GET("users")
    fun getUser() : Call<UserResponse>

    //유저 정보 수정 API
    @PATCH("users/infos/after")
    fun updateUser(@Body simpleUserModel: String) : Call<UserRegisterResponse>
}