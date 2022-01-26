package com.footprint.footprint.data.model

//기록 데이터 클래스(임시, API 연결 시 수정)
data class PostModel(
    var content: String = "",
    var photos: ArrayList<Int> = arrayListOf(),
    var time: String = "",
)
