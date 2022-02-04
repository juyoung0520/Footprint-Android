package com.footprint.footprint.data.remote.auth

import retrofit2.*
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginRetrofitInterface {
    @POST("/users/auth/login")
    //fun login(@Body userId: String, username: String, email: String, providerType: String): Call<LoginResponse>
    fun login(@Body socialUserData: SocialUserData): Call<LoginResponse>
}