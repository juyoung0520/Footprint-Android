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
    @SerializedName("courseMark") val courseMark: Int, // 찜 저장 수(인기 수)
    @SerializedName("courseLike") val courseLike: Int, // 좋아요 수
    @SerializedName("courseTags") val courseTags: List<String>,
    @SerializedName("courseImg") val courseImg: String,
    @SerializedName("userCourseLike") val userCourseLike: Boolean, // 유저가 해당코스 좋아요 여부
)
