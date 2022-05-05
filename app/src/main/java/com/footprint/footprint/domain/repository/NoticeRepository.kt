package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.data.dto.NoticeListDto
import com.footprint.footprint.data.dto.Result

interface NoticeRepository {
    suspend fun getNoticeList(page: Int, size: Int): Result<NoticeListDto>
    suspend fun getNotice(idx: Int): Result<NoticeDto>
}