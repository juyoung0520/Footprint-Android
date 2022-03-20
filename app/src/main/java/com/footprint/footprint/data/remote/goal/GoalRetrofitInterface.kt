package com.footprint.footprint.data.remote.goal

import com.footprint.footprint.data.model.BaseResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface GoalRetrofitInterface {
    @GET("users/goals")
    fun getThisMonthGoal(): Call<GetGoalResponse>

    @GET("users/goals/next")
    fun getNextMonthGoal(): Call<GetGoalResponse>

    @PATCH("users/goals")
    fun updateGoal(@Body updateGoal: RequestBody): Call<BaseResponse>
}