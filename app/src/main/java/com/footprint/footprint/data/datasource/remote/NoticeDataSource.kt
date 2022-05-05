package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result

interface NoticeDataSource {
    suspend fun getNoticeList(page: Int, size: Int): Result<BaseResponse>
    suspend fun getNotice(idx: Int): Result<BaseResponse>
}