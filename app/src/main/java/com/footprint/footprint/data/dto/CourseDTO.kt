package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName

// 코스 조회 API에서 사용
data class CourseDTO(
    @SerializedName("courseIdx") val courseIdx: String,
    @SerializedName("startLat") val startLat: Double,
    @SerializedName("startLong") val startLong: Double,
    @SerializedName("courseName") val courseName: String,
    @SerializedName("courseDist") val courseDist: Double,
    @SerializedName("courseTime") val courseTime: Int,
    @SerializedName("courseCount") val courseCount: Int, // 해당 코스를 경험한 전체 시람 수
    @SerializedName("courseLike") val courseLike: Int,   // 좋아요 수
    @SerializedName("courseTags") val courseTags: List<String>,
    @SerializedName("courseImg") val courseImg: String,
    @SerializedName("userCourseMark") val userCourseMark: Boolean, // 유저가 해당코스 북마크 여부
)

// 코스 상세 API에서 사용
data class CourseInfoDTO(
    @SerializedName("coordinate") val coordinate: List<ArrayList<Double>>, // 코스 동선 좌표들
    @SerializedName("courseDisc") val courseDisc: String,                  // 코스 상세 설명
)
