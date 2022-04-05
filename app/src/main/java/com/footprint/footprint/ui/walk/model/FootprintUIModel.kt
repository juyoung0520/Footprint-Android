package com.footprint.footprint.ui.walk.model

data class FootprintUIModel(
    var coordinate: List<Double> = arrayListOf(),   //발자국 좌표
    var recordAt: String = "", //발자국 작성 시간
    var write: String = "",   //발자국 내용
    var hashtagList: List<String>? = null,  //해시태그
    var photos: List<String> = arrayListOf(),   //사진
    var isMarked: Int = 1    //발자국 아이콘 여부
)