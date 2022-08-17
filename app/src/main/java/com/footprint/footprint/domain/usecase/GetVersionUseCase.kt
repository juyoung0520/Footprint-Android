package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.VersionDto
import com.footprint.footprint.domain.repository.NoticeRepository


class GetVersionUseCase(private val repository: NoticeRepository)  {
    suspend operator fun invoke(version: String): Result<VersionDto> = repository.getVersion(version)
}