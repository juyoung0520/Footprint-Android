package com.footprint.footprint.data.remote.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName ("code")val code: Int,
    @SerializedName ("message")val message: String,
    @SerializedName ("result")val result: String
)
data class Login(
    @SerializedName("jwtId") val jwtId: String,
    @SerializedName("status") val status: String,
    @SerializedName("checkMonthChanged") val checkMonthChanged: Boolean
)

data class UnRegisterResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName ("code")val code: Int,
    @SerializedName ("message")val message: String,
    @SerializedName ("result")val result: String
)