package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.*

interface NoticeRepository {
    suspend fun getNoticeList(page: Int, size: Int): Result<NoticeListDto>
    suspend fun getNotice(idx: Int): Result<NoticeDto>
    suspend fun getNewNotice(): Result<NewNoticeDto>
    suspend fun getKeyNotice(): Result<KeyNoticeDto>
}