package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.MapRemoteDataSource
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.mapper.MapMapper
import com.footprint.footprint.domain.model.AddressEntity
import com.footprint.footprint.domain.repository.MapRepository

class MapRepositoryImpl(private val dataSource: MapRemoteDataSource): MapRepository {
    override suspend fun getAddress(coords: String): Result<AddressEntity> {
        return when (val response = dataSource.getAddress(coords)) {
            is Result.Success -> {
                if (response.value.status.code==0)
                    Result.Success(MapMapper.mapperToAddressEntity(response.value))
                else
                    Result.GenericError(response.value.status.code, response.value.status.message)
            }
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }
}