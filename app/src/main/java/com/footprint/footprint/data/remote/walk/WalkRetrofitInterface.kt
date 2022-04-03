package com.footprint.footprint.data.remote.walk

import com.footprint.footprint.data.dto.BaseResponse
import retrofit2.Call
import retrofit2.http.*

interface WalkRetrofitInterface {
    /* calendarFragment */
    @GET("users/months/footprints")
    fun getMonthWalks(@Query("year")year: Int, @Query("month")month: Int): Call<MonthResponse>

    @GET("users/{date}")
    fun getDayWalks(@Path("date")date: String): Call<DayWalksResponse>

    @GET("users/tags")
    fun getTagWalkDates(@Query("tag")tag: String): Call<TagWalkDatesResponse>

    //산책 정보 삭제
    @PATCH("walks/{walkIdx}/status")
    fun deleteWalk(@Path("walkIdx") walkIdx: Int): Call<BaseResponse>
}