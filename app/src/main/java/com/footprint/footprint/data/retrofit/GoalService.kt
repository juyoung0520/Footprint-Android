package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface GoalService {
    @GET("users/goals")
    suspend fun getThisMonthGoal(): Response<BaseResponse>

    @GET("users/goals/next")
    suspend fun getNextMonthGoal(): Response<BaseResponse>

    @PATCH("users/goals")
    suspend fun updateGoal(@Body updateGoal: RequestBody): Response<BaseResponse>
}