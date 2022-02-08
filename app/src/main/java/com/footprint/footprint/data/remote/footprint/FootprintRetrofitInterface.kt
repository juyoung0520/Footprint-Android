package com.footprint.footprint.data.remote.footprint

import com.footprint.footprint.data.remote.walk.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface FootprintRetrofitInterface {
    @GET("footprints/{walkIdx}")
    fun getFootprints(@Path("walkIdx") walkIdx: Int): Call<GetFootprintsResponse>

    @Multipart
    @PATCH("footprints/{footprintIdx}")
    fun updateFootprint(
        @Path("footprintIdx") footprintIdx: Int,
        @PartMap data: HashMap<String, RequestBody>,
        @Part photos: List<MultipartBody.Part>
    ): Call<BaseResponse>
}