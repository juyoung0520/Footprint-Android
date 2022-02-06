package com.footprint.footprint.data.remote.badge
import com.footprint.footprint.data.remote.auth.Login
import com.google.gson.annotations.SerializedName

data class GetBadgeResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: BadgeResponse,
)

data class ChangeRepresentativeBadgeResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: BadgeInfo,
)

import com.footprint.footprint.data.remote.auth.Login
import com.google.gson.annotations.SerializedName

data class BadgeResponse(
    val repBadgeInfo: BadgeInfo,
    val badgeList: List<BadgeInfo>
)

data class BadgeInfo(
    val badgeIdx: Int,
    val badgeName: String,
    val badgeUrl: String,
    val badgeDate: String,
    val badgeOrder: String
)

/*이달의 뱃지 조회 API*/
data class MonthBadgeResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: MonthBadge?
)

data class MonthBadge(
    @SerializedName("badgeIdx") val badgeIdx: Int,      //뱃지 인덱스
    @SerializedName("badgeName") val badgeName: String, //뱃지 이름
    @SerializedName("badgeUrl") val badgeUrl: String,   //뱃지 이미지 url
    @SerializedName("badgeDate") val badgeDate: String  //뱃지 날짜 확인 컬럼
)

/*이달의 뱃지 조회 API*/
data class MonthBadgeResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: MonthBadge?
)

data class MonthBadge(
    @SerializedName("badgeIdx") val badgeIdx: Int,      //뱃지 인덱스
    @SerializedName("badgeName") val badgeName: String, //뱃지 이름
    @SerializedName("badgeUrl") val badgeUrl: String,   //뱃지 이미지 url
    @SerializedName("badgeDate") val badgeDate: String  //뱃지 날짜 확인 컬럼
)
