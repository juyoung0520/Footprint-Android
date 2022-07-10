package com.footprint.footprint.domain.model

data class GetFootprintEntity(
    val footprintIdx: Int,
    val recordAt: String,
    val write: String,
    val photoList: List<String>,
    val tagList: List<String>,
    val onWalk: Int,
    var footprintImgIdx: Int? = null    //발자국 아이콘 인덱스
)