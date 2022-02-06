package com.footprint.footprint.data.remote.footprint

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FootprintRetrofitInterface {
    @GET("footprints/{walkIdx}")
    fun getFootprints(@Path("walkIdx") walkIdx: Int): Call<GetFootprintsResponse>
}