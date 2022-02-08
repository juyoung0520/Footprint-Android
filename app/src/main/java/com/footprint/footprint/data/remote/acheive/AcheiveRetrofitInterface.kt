package com.footprint.footprint.data.remote.acheive

import com.footprint.footprint.data.remote.user.UserResponse
import retrofit2.*
import retrofit2.http.GET

interface AcheiveRetrofitInterface {

    //일별 정보 조회 API
    @GET("users/today")
    fun getToday() : Call<TodayResponse>

    //월별 정보 조회 API
    @GET("users/tmonth")
    fun getTMonth() : Call<TMonthResponse>
}