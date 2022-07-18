package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.UserRemoteDataSource
import com.footprint.footprint.data.datasource.remote.WeatherRemoteDataSource
import com.footprint.footprint.data.dto.WeatherDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.LocationModel
import com.footprint.footprint.domain.repository.WeatherRepository
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class WeatherRepositoryImpl(private val dataSource: WeatherRemoteDataSource): WeatherRepository {
    override suspend fun getWeather(location: LocationModel): Result<WeatherDTO> {

        val encryptedData = NetworkUtils.encrypt(location)
        LogUtils.d("WEATHER/API-DATA(E)", encryptedData)
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        return when(val response = dataSource.getWeather(data)){
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, WeatherDTO::class.java))
                else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
}