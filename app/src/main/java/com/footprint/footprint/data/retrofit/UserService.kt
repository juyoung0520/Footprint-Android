package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserService {
    //초기 정보 등록 API
    @POST("users/infos")
    suspend fun registerUser(@Body userModel: RequestBody): Response<BaseResponse>

    //유저 정보 조회 API
    @GET("users")
    suspend fun getUser() : Response<BaseResponse>

    //유저 정보 수정 API
    @PATCH("users/infos/after")
    suspend fun updateUser(@Body myInfoUserModel: RequestBody) : Response<BaseResponse>
}