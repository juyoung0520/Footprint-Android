package com.footprint.footprint.data.model

//User 데이터 클래스 (API 연결 시 수정)
data class User(
    var userIdx: String? = "",
    var nickname: String? = "",
    var email: String? = "",
    var gender: String? = "",
    var birthday: String? = ""
)

