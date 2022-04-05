package com.footprint.footprint.data.dto

import com.footprint.footprint.data.remote.achieve.InfoAchieveResult
import com.footprint.footprint.data.remote.achieve.InfoStatResult
import com.google.gson.annotations.SerializedName

data class AchieveDetailResult(
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
