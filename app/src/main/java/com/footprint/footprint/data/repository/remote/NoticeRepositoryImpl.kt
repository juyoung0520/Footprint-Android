package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.NoticeDataSource
import com.footprint.footprint.data.dto.KeyNoticeDto
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.data.dto.NoticeListDto
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.NoticeRepository
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import com.footprint.footprint.utils.getReadNoticeList
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class NoticeRepositoryImpl(private val dataSource: NoticeDataSource): NoticeRepository {

    override suspend fun getNoticeList(page: Int, size: Int): Result<NoticeListDto> {
        return when(val response = dataSource.getNoticeList(page, size)){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, NoticeListDto::class.java))
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

    override suspend fun getNewNotice(): Result<Boolean> {
        val type = object : TypeToken<Boolean>() {}.type

        return when(val response = dataSource.getNewNotice()){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, type))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun getKeyNotice(): Result<List<KeyNoticeDto>> {

        val encryptedData = NetworkUtils.encrypt(getReadNoticeList() as List<Int>)
        LogUtils.d("NOTICE/API-DATA(E)", encryptedData)
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        val type = object : TypeToken<List<KeyNoticeDto>>() {}.type
        return when(val response = dataSource.getKeyNotice(data)){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, type))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

}