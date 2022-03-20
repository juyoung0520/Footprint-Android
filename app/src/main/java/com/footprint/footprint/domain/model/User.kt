package com.footprint.footprint.domain.model

import com.google.gson.annotations.SerializedName

data class SimpleUser(
    var nickname: String = "",   //닉네임(*)
    @SerializedName("sex") var gender: String = "null",  //성별(*): female, male, null
    var birth: String = "",  //생년월일: yyyy-MM-dd
    var height: Int = 0,    //키: 0
    var weight: Int = 0,    //몸무게: 0
)