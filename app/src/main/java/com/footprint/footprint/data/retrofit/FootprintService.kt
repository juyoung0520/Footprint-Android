package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface FootprintService {
    //산책별 발자국 리스트 조회
    @GET("footprints/{walkIdx}")
    suspend fun getFootprints(@Path("walkIdx") walkIdx: Int): Response<BaseResponse>

    //발자국 정보 수정
    @Multipart
    @PATCH("footprints/{walkIdx}/{footprintIdx}")
    suspend fun updateFootprint(
        @Path("walkIdx") walkIdx: Int,
        @Path("footprintIdx") footprintIdx: Int,
        @PartMap data: HashMap<String, RequestBody>?,
        @Part photos: List<MultipartBody.Part>?
    ): Response<BaseResponse>
}