package com.footprint.footprint.domain.model

data class SaveWalkEntity(
    var walkIdx: Int = 0,
    var walkTitle: String = "", //00번째 산책
    var walkTime: String = "",  //산책 시간
    var startAt: String = "",
    var endAt: String = "",
    var distance: Double = 0.0,
    var coordinate: List<List<Double>> = arrayListOf(),
    var calorie: Int = 0,
    var pathImg: String = "",
    var saveWalkFootprints: ArrayList<SaveWalkFootprintEntity> = arrayListOf()
)

data class SaveWalkFootprintEntity(
    var coordinates: List<Double> = arrayListOf(),   //발자국 좌표
    var recordAt: String = "", //발자국 작성 시간
    var write: String = "",   //발자국 내용
    var hashtagList: ArrayList<String> = arrayListOf(),  //해시태그
    var photos: ArrayList<String> = arrayListOf(),   //사진
    var onWalk: Int = 1,    //발자국 아이콘 여부
    var footprintImgIdx: Int? = null    //발자국 아이콘 인덱스
)