package com.footprint.footprint.data.model

import com.google.gson.annotations.SerializedName

/*Social User Model */
data class SocialUserModel(
    @SerializedName("userId") val userId: String,               //소셜로그인 Id
    @SerializedName("username") val username: String,           //소셜로그인 이름
    @SerializedName("email") val email: String,                 //소셜로그인 이메일
    @SerializedName("providerType") val providerType: String    //소셜로그인 종류(kakao, google)
)
