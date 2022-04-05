package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("jwtId") val jwtId: String,
    @SerializedName("status") val status: String,
    @SerializedName("checkMonthChanged") val checkMonthChanged: Boolean
)
