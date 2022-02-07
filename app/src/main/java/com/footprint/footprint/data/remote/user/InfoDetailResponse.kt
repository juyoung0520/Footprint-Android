package com.footprint.footprint.data.remote.user

import com.footprint.footprint.data.model.GoalModel
import com.google.gson.annotations.SerializedName

// 나중에 user -> infos 안으로
data class InfoDetailResult(
    @SerializedName("userInfoAchieve")val userInfoAchieve: InfoAchieveResult,
    @SerializedName("getUserGoalRes")val getUserGoalRes: GoalModel,
    @SerializedName("userInfoStat")val userInfoStat: InfoStatResult,
)

data class InfoAchieveResult(
    @SerializedName("todayGoalRate")val todayGoalRate: Int,
    @SerializedName("monthGoalRate")val monthGoalRate: Int,
    @SerializedName("userWalkCount")val userWalkCount: Int,
)

data class InfoStatResult(
    @SerializedName("mostWalkDay")val mostWalkDay: List<String>,
    @SerializedName("userWeekDayRate")val userWeekDayRate: List<Double>,
    @SerializedName("thisMonthWalkCount")val thisMonthWalkCount: Int,
    @SerializedName("monthlyWalkCount")val monthlyWalkCount: List<Int>,
    @SerializedName("thisMonthGoalRate")val thisMonthGoalRate: Int,
    @SerializedName("monthlyGoalRate")val monthlyGoalRate: List<Int>,
)

data class InfoDetailResponse(
    @SerializedName("isSuccess")val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: InfoDetailResult
)
