package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.repository.NoticeRepository

class GetNoticeUseCase(private val repository: NoticeRepository) {
    suspend operator fun invoke(idx: Int): Result<NoticeDto> = repository.getNotice(idx)
}