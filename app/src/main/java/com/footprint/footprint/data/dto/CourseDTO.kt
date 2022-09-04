package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName
import com.naver.maps.geometry.LatLng

/* 수정 : */
data class CourseDTO(
    @SerializedName("courseIdx") val courseIdx: String,
    @SerializedName("startLat") val startLat: Double,
    @SerializedName("startLong") val startLong: Double,
    @SerializedName("courseName") val courseName: String,
    @SerializedName("courseDist") val courseDist: Double,
    @SerializedName("courseTime") val courseTime: Int,
    @SerializedName("courseCount") val courseCount: Int, // 해당 코스를 경험한 전체 사람 수
    @SerializedName("courseLike") val courseLike: Int, // 좋아요 수
    @SerializedName("courseTags") val courseTags: List<String>,
    @SerializedName("courseImg") val courseImg: String,
    @SerializedName("userCourseMark") val userCourseMark: Boolean, // 유저가 해당코스 북마크 여부
)
