package com.footprint.footprint.data.remote.users

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*사용자 정보 조회*/
data class UserResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: User?
)

data class User(
    @SerializedName("userIdx") val userIdx: Int,      //유저 인덱스
    @SerializedName("nickname") val nickname: String, //어플에서 사용할 닉네임
    @SerializedName("name") val name: String,         //본명(소셜 계정 이름)
    @SerializedName("email") val email: String,       //이메일(소셜 계정 이메일)
    @SerializedName("status") val status: String,     //유저 상태 (활성화, 비활성화, 블랙)
    @SerializedName("badgeIdx") val badgeIdx: Int,    //뱃지 인덱스
    @SerializedName("badgeUrl") val badgeUrl: String, //뱃지 이미지 URL
    @SerializedName("age") val age: Int,              // 나이
    @SerializedName("sex") val sex: String,           //성별(male, female)
    @SerializedName("height") val height: Int,        //키
    @SerializedName("weight") val weight: Int         //몸무게
)

/*오늘 정보 조회*/
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

/*이번달 정보 조회*/
data class TMonthResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: TMonth?
)

data class TMonth(
    @SerializedName("goalDayList") val goalDayList: ArrayList<String>,      //목표요일
    @SerializedName("getDayRateRes")val getDayRateRes: ArrayList<TMonthDayRateRes>?,     //이번달 산책 날짜(&달성율)
    @SerializedName("getMonthTotal")val getMonthTotal: TMonthGoal?,     //이번달 목표 달성율
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

