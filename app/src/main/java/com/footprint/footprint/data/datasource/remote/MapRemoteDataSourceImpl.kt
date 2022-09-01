package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.MapDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.MapService

class MapRemoteDataSourceImpl(private val api: MapService): BaseRepository(), MapRemoteDataSource {
    override suspend fun getAddress(coords: String): Result<MapDTO> {
        return safeApiCall2 { api.getAddress(coords) }
    }
}