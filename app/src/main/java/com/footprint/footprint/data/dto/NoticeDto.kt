package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

// 공지사항 목록 조회 DTO
data class NoticeListDto(
    @SerializedName("pageOn")val pageOn: Int,
    @SerializedName("pageTotal")val pageTotal: Int,
    @SerializedName("noticeList")val noticeList: ArrayList<NoticeInfo>,
)

data class NoticeInfo(
    @SerializedName("noticeIdx")val noticeIdx: Int,
    @SerializedName("title")val title: String,
    @SerializedName("isNewNotice")val isNewNotice: Boolean,
    @SerializedName("createAt")val createAt: LocalDateTime,
    @SerializedName("updateAt")val updateAt: LocalDateTime
)

// 공지사항 조회 DTO
data class NoticeDto(
    @SerializedName("noticeIdx")val noticeIdx: Int,
    @SerializedName("title")val title: String,
    @SerializedName("notice")val notice: String?,
    @SerializedName("image")val image: String?,
    @SerializedName("isNewNotice")val isNewNotice: Boolean,
    @SerializedName("prevIdx")val prevIdx: Int?, // null이면 없는 것 (제일 첫 글)
    @SerializedName("nextIdx")val nextIdx: Int?, // null이면 없는 것 (제일 마지막 글)
    @SerializedName("createAt")val createAt: LocalDateTime,
    @SerializedName("updateAt")val updateAt: LocalDateTime,
)

// 중요 공지사항 조회 DTO
data class KeyNoticeDto(
    @SerializedName("noticeIdx")val noticeIdx: Int,
    @SerializedName("title")val title: String,
    @SerializedName("notice")val notice: String,
    @SerializedName("key")val key: Boolean,
    @SerializedName("image")val image: String,
    @SerializedName("createAt")val createAt: LocalDateTime,
    @SerializedName("updateAt")val updateAt: LocalDateTime,
)