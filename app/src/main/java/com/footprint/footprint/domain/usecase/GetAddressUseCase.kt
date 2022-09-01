package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.AddressEntity
import com.footprint.footprint.domain.repository.MapRepository

class GetAddressUseCase(private val repository: MapRepository) {
    suspend fun invoke(coords: String): Result<AddressEntity> = repository.getAddress(coords)
}