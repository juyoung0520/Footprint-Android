package com.footprint.footprint.retrofit

import com.footprint.footprint.data.model.BaseResponse
import com.footprint.footprint.data.remote.goal.GetGoalResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface GoalService {
    @GET("users/goals")
    suspend fun getThisMonthGoal(): Response<BaseResponse>

    @GET("users/goals/next")
    fun getNextMonthGoal(): Call<GetGoalResponse>

    /*@PATCH("users/goals")
    fun updateGoal(@Body updateGoal: RequestBody): Call<BaseResponse>*/
}