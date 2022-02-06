package com.footprint.footprint.data.remote.users

import retrofit2.*
import retrofit2.http.GET

interface UserRetrofitInterface {
    //유저 정보 조회 API
     @GET("users")
     fun getUser() : Call<UserResponse>

    //일별 정보 조회 API
    @GET("users/today")
    fun getToday() : Call<TodayResponse>

    //월별 정보 조회 API
    @GET("users/tmonth")
    fun getTMonth() : Call<TMonthResponse>
}