package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName
import com.naver.maps.geometry.LatLng

/* 수정 : */
data class CourseDTO(
    @SerializedName("id") val id: String,
    @SerializedName("latLng") val latLng: LatLng
)
