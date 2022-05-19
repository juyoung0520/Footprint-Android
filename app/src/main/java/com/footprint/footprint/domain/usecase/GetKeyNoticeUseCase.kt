package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.KeyNoticeDto
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.NoticeRepository

class GetKeyNoticeUseCase(private val repository: NoticeRepository) {
    suspend fun invoke(): Result<KeyNoticeDto> = repository.getKeyNotice()
}