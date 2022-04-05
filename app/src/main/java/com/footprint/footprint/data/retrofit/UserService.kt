package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH

interface UserService {
    //유저 정보 수정 API
    @PATCH("users/infos/after")
    suspend fun updateUser(@Body user: RequestBody) : Response<BaseResponse>
}