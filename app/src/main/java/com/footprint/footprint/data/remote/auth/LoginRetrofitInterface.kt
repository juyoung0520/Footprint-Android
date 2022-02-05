package com.footprint.footprint.data.remote.auth

import com.footprint.footprint.data.model.SocialUserModel
import retrofit2.*
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginRetrofitInterface {

    //로그인 API
    @POST("users/auth/login")
    fun login(@Body socialUserData: SocialUserModel): Call<LoginResponse>

    //회원 탈퇴 API
}