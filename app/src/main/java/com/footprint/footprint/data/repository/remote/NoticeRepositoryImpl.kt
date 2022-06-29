package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.NoticeDataSource
import com.footprint.footprint.data.dto.*
import com.footprint.footprint.domain.model.ReadNoticeListModel
import com.footprint.footprint.domain.repository.NoticeRepository
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import com.footprint.footprint.utils.getReadNoticeList
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class NoticeRepositoryImpl(private val dataSource: NoticeDataSource): NoticeRepository {

    override suspend fun getNoticeList(page: Int, size: Int): Result<NoticeListDto> {
        return when(val response = dataSource.getNoticeList(page, size)){
            is Result.Success -> {
                if (response.value.isSuccess){
                    Result.Success(NetworkUtils.decrypt(response.value.result, NoticeListDto::class.java))
                }
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun getNotice(idx: Int): Result<NoticeDto> {
        return when(val response = dataSource.getNotice(idx)){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, NoticeDto::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun getNewNotice(): Result<NewNoticeDto> {
        return when(val response = dataSource.getNewNotice()){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, NewNoticeDto::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun getKeyNotice(): Result<KeyNoticeDto> {
        val readNoticeList = ReadNoticeListModel(getReadNoticeList())
        val encryptedData = NetworkUtils.encrypt(readNoticeList)
        LogUtils.d("NOTICE/API-DATA(E)", encryptedData)
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        return when(val response = dataSource.getKeyNotice(data)){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, KeyNoticeDto::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

}