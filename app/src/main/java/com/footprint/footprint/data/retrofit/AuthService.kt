package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    //자동 로그인 API
    @GET("users/autologin")
    suspend fun autoLogin(): Response<BaseResponse>

    //로그인 API
    @POST("users/auth/login")
    suspend fun login(@Body socialUserData: RequestBody): Response<BaseResponse>

    //회원 탈퇴 API
    @DELETE("users/unregister")
    suspend fun unregister(): Response<BaseResponse>
}