package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.WeatherService
import com.footprint.footprint.utils.LogUtils
import okhttp3.RequestBody

class WeatherRemoteDataSourceImpl(private val api: WeatherService): BaseRepository(), WeatherRemoteDataSource {
    override suspend fun getWeather(location: RequestBody): Result<BaseResponse> {
        LogUtils.d("WeatherRemoteDataSourceImpl", "getWeather")
        return safeApiCall() { api.getWeather(location).body()!! }
    }
}