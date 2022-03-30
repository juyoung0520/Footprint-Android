package com.footprint.footprint.data.remote.user

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.RequestBody
import retrofit2.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserRetrofitInterface  {
    //유저 정보 조회 API
    @GET("users")
    fun getUser() : Call<BaseResponse>

}