package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.KeyNoticeDto
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.MyInfoUserModel
import com.footprint.footprint.domain.repository.NoticeRepository
import com.footprint.footprint.domain.repository.UserRepository

class GetKeyNoticeUseCase(private val repository: NoticeRepository) {
    suspend fun invoke(): Result<List<KeyNoticeDto>> = repository.getKeyNotice()
}