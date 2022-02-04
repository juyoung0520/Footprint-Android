package com.footprint.footprint.data.model

import java.io.Serializable

//User 데이터 클래스 (API 연결 시 수정)
data class UserModel(
    var nickname: String? = null,  //닉네임(*)
    var gender: String? = null,    //성별(*): female, male, null
    var birth: String? = null,     //생년월일: 0000.00.00
    var height: Int? = null,       //키
    var weight: Int? = null,        //몸무게
    var goalDay: ArrayList<Int> = arrayListOf(), //목표 산책 요일
    var goalWalkTime: Int = 0,   //목표 산책 시간(분)
    var walkTimeSlot: Int = 0    //산책 시간대(1:이른오전/2:늦은오전/.../6:새벽/7:매번다름)
): Serializable

