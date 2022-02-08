package com.footprint.footprint.data.remote.auth

import com.footprint.footprint.data.model.SocialUserModel
import retrofit2.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginRetrofitInterface {
    //자동 로그인 API
    @GET("users/autologin")
    fun autoLogin(): Call<LoginResponse>


    //로그인 API
    @POST("users/auth/login")
    fun login(@Body socialUserData: SocialUserModel): Call<LoginResponse>

    //회원 탈퇴 API
}