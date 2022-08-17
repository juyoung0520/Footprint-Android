package com.footprint.footprint.domain.model

import com.google.gson.annotations.SerializedName

// 읽은 주요 공지사항 리스트
data class ReadNoticeListModel(
    @SerializedName("checkedKeyNoticeIdxList") val checkedKeyNoticeIdxList: List<Int>
)
