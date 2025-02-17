package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.AddressEntity

interface MapRepository {
    suspend fun getAddress(coords: String): Result<AddressEntity>
}