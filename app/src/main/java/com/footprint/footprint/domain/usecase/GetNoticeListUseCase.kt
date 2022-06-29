package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.NoticeListDto
import com.footprint.footprint.domain.repository.NoticeRepository
import com.footprint.footprint.data.dto.Result

class GetNoticeListUseCase(private val repository: NoticeRepository) {
    suspend operator fun invoke(page: Int, size: Int): Result<NoticeListDto> = repository.getNoticeList(page, size)
}