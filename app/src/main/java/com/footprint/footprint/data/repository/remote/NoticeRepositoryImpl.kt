package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.NoticeDataSource
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.data.dto.NoticeListDto
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.NoticeRepository
import com.footprint.footprint.utils.NetworkUtils

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

}