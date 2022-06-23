package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.MapDTO
import retrofit2.Response
import retrofit2.http.*

interface MapService {
    //날씨 API
    @GET("map-reversegeocode/v2/gc?request=coordsToaddr&output=json&orders=admcode")
    suspend fun getAddress(@Query("coords") coords: String): Response<MapDTO>
}