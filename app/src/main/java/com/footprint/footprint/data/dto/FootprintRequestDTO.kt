package com.footprint.footprint.data.dto

data class FootprintRequestDTO(
    var coordinates: List<Double> = arrayListOf(),   //발자국 좌표
    var recordAt: String = "", //발자국 작성 시간
    var write: String = "",   //발자국 내용
    var hashtagList: List<String>? = null,  //해시태그
    var photos: ArrayList<String> = arrayListOf(),   //사진
    var onWalk: Int = 1    //발자국 아이콘 여부
)