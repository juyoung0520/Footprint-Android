package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface WalkService {
    //산책 정보 조회
    @GET("walks/{walkidx}")
    suspend fun getWalkByIdx(@Path("walkidx") walkIdx: Int): Response<BaseResponse>

    //산책 정보 삭제
    @PATCH("walks/{walkIdx}/status")
    suspend fun deleteWalk(@Path("walkIdx") walkIdx: Int): Response<BaseResponse>

    //산책 저장
    @Multipart
    @POST("walks")
    suspend fun writeWalk(
        @Part("walk") walk: RequestBody,
        @Part("footprintList") footprintList: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): Response<BaseResponse>
}