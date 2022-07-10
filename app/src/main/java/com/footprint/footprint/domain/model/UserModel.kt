package com.footprint.footprint.domain.model

import com.google.gson.annotations.SerializedName

/*Social User Model: 로그인 API(소셜 로그인 정보) */
data class SocialUserModel(
    @SerializedName("userId") val userId: String,               //소셜로그인 Id
    @SerializedName("username") val username: String,           //소셜로그인 이름
    @SerializedName("email") val email: String,                 //소셜로그인 이메일
    @SerializedName("providerType") val providerType: String    //소셜로그인 종류(kakao, google)
)

/*Init User Model: 초기 정보 등록 API*/
data class InitUserModel(
    @SerializedName("nickname") var nickname: String = "",                  //닉네임(*)
    @SerializedName("sex") var gender: String = "null",                     //성별(*): female, male, null
    @SerializedName("birth") var birth: String = "0000-00-00",              //생년월일: yyyy-MM-dd
    @SerializedName("height") var height: Int ?= null,                      //키: 0
    @SerializedName("weight") var weight: Int ?= null,                      //몸무게: 0
    @SerializedName("dayIdx") var goalDay: ArrayList<Int> = arrayListOf(),  //목표 산책 요일
    @SerializedName("walkGoalTime") var goalWalkTime: Int? = null,          //목표 산책 시간(분)
    @SerializedName("walkTimeSlot") var walkTimeSlot: Int? = null,          //산책 시간대(1:이른오전/2:늦은오전/.../6:새벽/7:매번다름)
)

/*MyInfo User Model: 내 정보 조회, 수정 API*/
data class MyInfoUserModel(
    @SerializedName("nickname") var nickname: String = "",                  //닉네임(*)
    @SerializedName("sex") var gender: String = "null",                     //성별(*): female, male, null
    @SerializedName("birth") var birth: String = "0000-00-00",              //생년월일: yyyy-MM-dd
    @SerializedName("height") var height: Int = 0,                          //키: 0
    @SerializedName("weight") var weight: Int = 0,                          //몸무게: 0
)

/*Simple User Model: 홈, 산책 화면, 마이페이지에서 사용하는 유저 정보*/
data class SimpleUserModel(
    var nickname: String = "",
    var gender: String = "null",
    var height: Int? = null,
    var weight: Int? = null,
    var goalWalkTime: Int? = null,
    var walkNumber: Int? = null,
    var badgeUrl: String? = ""
)