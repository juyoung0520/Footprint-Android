package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.MapDTO
import com.footprint.footprint.data.dto.Result

interface MapRemoteDataSource {
    suspend fun getAddress(coords: String): Result<MapDTO>
}