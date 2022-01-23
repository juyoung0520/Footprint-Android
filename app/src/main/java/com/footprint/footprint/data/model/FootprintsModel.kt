package com.footprint.footprint.data.model

import java.util.ArrayList

data class FootprintsModel(
    var footprints: ArrayList<FootprintModel> = arrayListOf()
)

//기록 데이터 클래스(임시, API 연결 시 수정)
data class FootprintModel(
    var content: String = "",
    var photos: ArrayList<String> = arrayListOf(),
    var time: String = "",
    var hashTags: ArrayList<String> = arrayListOf(),
    var isUpdate: Boolean = false   //추가: false, 수정: true
)
