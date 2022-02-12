package com.footprint.footprint.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*User Model: 초기 정보 등록 API*/
data class UserModel(
    @SerializedName("nickname") var nickname: String = "",                  //닉네임(*)
    @SerializedName("sex") var gender: String = "null",                     //성별(*): female, male, null
    @SerializedName("birth") var birth: String = "0000-00-00",              //생년월일: yyyy-MM-dd
    @SerializedName("height") var height: Int? = null,                          //키: 0
    @SerializedName("weight") var weight: Int? = null,                          //몸무게: 0
    @SerializedName("dayIdx") var goalDay: ArrayList<Int> = arrayListOf(),  //목표 산책 요일
    @SerializedName("walkGoalTime") var goalWalkTime: Int? = null,              //목표 산책 시간(분)
    @SerializedName("walkTimeSlot") var walkTimeSlot: Int? = null,               //산책 시간대(1:이른오전/2:늦은오전/.../6:새벽/7:매번다름)
    var walkNumber: Int ?= 0                                                       //산책 횟수
)

/*Simple User Model: 정보 수정 API*/
data class SimpleUserModel(
    @SerializedName("nickname") var nickname: String = "",                  //닉네임(*)
    @SerializedName("sex") var gender: String = "null",                     //성별(*): female, male, null
    @SerializedName("birth") var birth: String = "0000-00-00",              //생년월일: yyyy-MM-dd
    @SerializedName("height") var height: Int = 0,                          //키: 0
    @SerializedName("weight") var weight: Int = 0,                          //몸무게: 0
)