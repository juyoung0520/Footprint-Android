package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.NoticeService
import com.footprint.footprint.utils.LogUtils
import okhttp3.RequestBody

class NoticeDataSourceImpl(private val api: NoticeService): BaseRepository(), NoticeDataSource {
    override suspend fun getNoticeList(page: Int, size: Int): Result<BaseResponse> {
        LogUtils.d("NoticeDataSourceImpl", "getNoticeList")
        return safeApiCall { api.getNoticeList(page, size).body()!! }
    }

    override suspend fun getNotice(idx: Int): Result<BaseResponse> {
        LogUtils.d("NoticeDataSourceImpl", "getNotice")
        return safeApiCall { api.getNotice(idx).body()!! }
    }

    override suspend fun getNewNotice(): Result<BaseResponse> {
        LogUtils.d("NoticeDataSourceImpl", "getNewNotice")
        return safeApiCall { api.getNewNotice().body()!! }
    }

    override suspend fun getKeyNotice(list: RequestBody): Result<BaseResponse> {
        LogUtils.d("NoticeDataSourceImpl", "getKeyNotice")
        return safeApiCall { api.getKeyNotice(list).body()!! }
    }
}