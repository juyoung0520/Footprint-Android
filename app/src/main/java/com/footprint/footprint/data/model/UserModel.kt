package com.footprint.footprint.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*User Model*/
data class UserModel(
    @SerializedName("nickname") var nickname: String = "",                  //닉네임(*)
    @SerializedName("sex") var gender: String = "null",                     //성별(*): female, male, null
    @SerializedName("birth") var birth: String = "0000-00-00",              //생년월일: yyyy-MM-dd
    @SerializedName("height") var height: Int = 0,                          //키: 0
    @SerializedName("weight") var weight: Int = 0,                          //몸무게: 0
    @SerializedName("dayIdx") var goalDay: ArrayList<Int> = arrayListOf(),  //목표 산책 요일
    @SerializedName("walkGoalTime") var goalWalkTime: Int = 0,              //목표 산책 시간(분)
    @SerializedName("walkTimeSlot") var walkTimeSlot: Int = 0               //산책 시간대(1:이른오전/2:늦은오전/.../6:새벽/7:매번다름)
)

