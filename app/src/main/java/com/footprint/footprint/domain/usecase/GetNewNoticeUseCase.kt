package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.NewNoticeDto
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.Today
import com.footprint.footprint.domain.repository.AchieveRepository
import com.footprint.footprint.domain.repository.NoticeRepository


class GetNewNoticeUseCase(private val repository: NoticeRepository)  {
    suspend fun invoke(): Result<NewNoticeDto> = repository.getNewNotice()
}