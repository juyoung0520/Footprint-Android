package com.footprint.footprint.data.remote.acheive

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/*일별 정보 조회*/
data class TodayResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: Today?
)

data class Today(
    @SerializedName("goalRate") val goalRate: Float,      //오늘 목표 달성량
    @SerializedName("walkGoalTime")val walkGoalTime: Int, //오늘 산책 목표 시간
    @SerializedName("walkTime")val walkTime: Int,         //오늘 산책 누적 시간
    @SerializedName("distance")val distance: Double,      //오늘 산책 누적 거리
    @SerializedName("calorie")val calorie: Int,           //오늘 산책 누적 칼로리
)

/*월별 정보 조회*/
data class TMonthResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: TMonth?
)

data class TMonth(
    @SerializedName("goalDayList") val goalDayList: ArrayList<String>,      //목표요일
    @SerializedName("getDayRateRes")val getDayRateRes: ArrayList<TMonthDayRateRes>?,     //이번달 산책 날짜(&달성율)
    @SerializedName("getMonthTotal")val getMonthTotal: TMonthGoal,     //이번달 목표 달성율
)

data class TMonthDayRateRes(
    @SerializedName("day") val day: Int,        //산책한 날짜
    @SerializedName("rate") val rate: Float     //해당 날짜의 산책 달성률
): Serializable

data class TMonthGoal(
    @SerializedName("monthTotalMin") val monthTotalMin: Int,            //이번달 총 산책 시간(분)
    @SerializedName("monthTotalDistance") val monthTotalDistance: Float,//이번달 총 산책 거리(km)
    @SerializedName("monthPerCal") val monthPerCal: Int                 //이번달 평균 소모 칼로리
)

