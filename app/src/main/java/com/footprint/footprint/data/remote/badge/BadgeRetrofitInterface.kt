package com.footprint.footprint.data.remote.badge

import retrofit2.Call
import retrofit2.http.GET

interface BadgeRetrofitInterface {
    /*이달의 뱃지 조회 API*/
    @GET("users/badges/status")
    fun getMonthBadge(): Call<MonthBadgeResponse>
}