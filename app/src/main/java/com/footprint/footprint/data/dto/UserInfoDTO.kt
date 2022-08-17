package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName

data class UserInfoDTO(
    @SerializedName("userInfoAchieve")val userInfoAchieve: UserInfoAchieveDTO,
    @SerializedName("getUserGoalRes")val getUserGoalRes: GoalModel,
    @SerializedName("userInfoStat")val userInfoStat: UserInfoStatDTO,
)

data class UserInfoAchieveDTO(
    @SerializedName("todayGoalRate")val todayGoalRate: Int,
    @SerializedName("monthGoalRate")val monthGoalRate: Int,
    @SerializedName("userWalkCount")val userWalkCount: Int,
)

data class UserInfoStatDTO(
    @SerializedName("mostWalkDay")val mostWalkDay: List<String>,
    @SerializedName("userWeekDayRate")val userWeekDayRate: List<Double>,
    @SerializedName("thisMonthWalkCount")val thisMonthWalkCount: Int,
    @SerializedName("monthlyWalkCount")val monthlyWalkCount: List<Int>,
    @SerializedName("thisMonthGoalRate")val thisMonthGoalRate: Int,
    @SerializedName("monthlyGoalRate")val monthlyGoalRate: List<Int>,
)
