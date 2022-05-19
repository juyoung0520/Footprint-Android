package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

// 공지사항 목록 조회 DTO
data class NoticeListDto(
    @SerializedName("pageOn")val pageOn: Int,
    @SerializedName("pageTotal")val pageTotal: Int,
    @SerializedName("noticeList")val noticeList: Array<NoticeInfoDto>,
)

data class NoticeInfoDto(
    @SerializedName("noticeIdx")val noticeIdx: Int,
    @SerializedName("title")val title: String,
    @SerializedName("isNewNotice")val isNewNotice: Boolean,
    @SerializedName("createAt")val createAt: String,
    @SerializedName("updateAt")val updateAt: String
)

// 공지사항 조회 DTO
data class NoticeDto(
    @SerializedName("noticeIdx")val noticeIdx: Int,
    @SerializedName("title")val title: String,
    @SerializedName("notice")val notice: String?,
    @SerializedName("image")val image: String?,
    @SerializedName("isNewNotice")val isNewNotice: Boolean,
    @SerializedName("preIdx")val preIdx: Int,
    @SerializedName("postIdx")val postIdx: Int,
    @SerializedName("createAt")val createAt: String,
    @SerializedName("updateAt")val updateAt: String,
)

// 새로운 공지사항 조회 DTO
data class NewNoticeDto(
    @SerializedName("noticeNew") val noticeNew: Boolean
)

// 중요 공지사항 조회 DTO
data class KeyNoticeDto(
    @SerializedName("keyNoticeList") val keyNoticeList: List<NoticeDto>
)



