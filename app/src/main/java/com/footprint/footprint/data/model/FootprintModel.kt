package com.footprint.footprint.data.model

import java.util.*

data class FootprintsModel(
    val footprints: ArrayList<FootprintModel> = arrayListOf()
)

//기록 데이터 클래스(임시, API 연결 시 수정)
data class FootprintModel(
    var coordinate: List<Double> = arrayListOf(),   //발자국 좌표
    var recordAt: String = "", //발자국 작성 시간
    var write: String = "",   //발자국 내용
    var hashtagList: List<String>? = null,  //해시태그
    var photos: List<String> = arrayListOf(),   //사진
    var isMarked: Boolean = true    //발자국 아이콘 여부
)
