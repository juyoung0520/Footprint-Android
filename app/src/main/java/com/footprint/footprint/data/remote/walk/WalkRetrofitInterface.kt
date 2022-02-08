package com.footprint.footprint.data.remote.walk

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WalkRetrofitInterface {
    /* calendarFragment */
    @GET("/users/months/footprints")
    fun getMonthWalks(@Query("year")year: Int, @Query("month")month: Int): Call<MonthResponse>

    @GET("/users/{date}")
    fun getDayWalks(@Path("date")date: String): Call<DayWalksResponse>
}