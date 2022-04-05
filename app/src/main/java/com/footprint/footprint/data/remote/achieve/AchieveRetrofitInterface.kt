package com.footprint.footprint.data.remote.achieve

import retrofit2.*
import retrofit2.http.GET

interface AchieveRetrofitInterface {
    // 마이페이지 통계 API
    @GET("users/infos")
    fun getInfoDetail(): Call<AchieveDetailResponse>
}