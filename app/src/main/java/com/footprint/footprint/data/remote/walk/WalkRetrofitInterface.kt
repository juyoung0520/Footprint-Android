package com.footprint.footprint.data.remote.walk

import com.footprint.footprint.data.dto.BaseResponse
import retrofit2.Call
import retrofit2.http.*

interface WalkRetrofitInterface {
    /* calendarFragment */
    @GET("users/tags")
    fun getTagWalkDates(@Query("tag")tag: String): Call<TagWalkDatesResponse>
}