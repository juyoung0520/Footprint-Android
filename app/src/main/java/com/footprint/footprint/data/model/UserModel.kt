package com.footprint.footprint.data.model

import java.io.Serializable

//User 데이터 클래스 (API 연결 시 수정)
data class UserModel(
    var idToken: String = "",      //소셜 로그인 시 받는 userToken(*)
    var nickname: String? = null,  //닉네임(*)
    var gender: String? = null,    //성별(*): female, male, null
    var birth: String? = null,     //생년월일: 0000.00.00
    var height: Int? = null,       //키
    var weight: Int? = null,        //몸무게
    var goalDay: ArrayList<String> = arrayListOf(), //목표 산책 요일
    var goalWalkTime: String? = null,   //목표 산책 시간
    var walkTimeSlot: String? = null    //산책 시간대
): Serializable

