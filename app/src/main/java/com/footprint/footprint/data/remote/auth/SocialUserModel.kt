package com.footprint.footprint.data.remote.auth

import com.google.gson.annotations.SerializedName

/*로그인 API Body에 들어갈 Social User Model
* 소셜로그인 후 받은 정보들 -> userId, username, email
* 소셜로그인 종류 -> providerType
* */
data class SocialUserModel(
    @SerializedName("userId") val userId: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("providerType") val providerType: String
)
