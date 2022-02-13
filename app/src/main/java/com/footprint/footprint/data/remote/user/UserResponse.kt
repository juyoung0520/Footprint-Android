package com.footprint.footprint.data.remote.user

import com.google.gson.annotations.SerializedName

/*정보 등록, 수정*/
data class UserRegisterResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: String
)

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
    @SerializedName("birth") val birth: String,       //생일
    @SerializedName("age") val age: Int,              //나이
    @SerializedName("sex") val sex: String,           //성별(male, female)
    @SerializedName("height") val height: Int,        //키
    @SerializedName("weight") val weight: Int,         //몸무게
    @SerializedName("walkNumber") val walkNumber: Int  //산책 횟수
)
