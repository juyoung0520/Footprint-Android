package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.model.Result
import okhttp3.RequestBody
import retrofit2.http.Body

interface WeatherRemoteDataSource {
    suspend fun getWeather(@Body location: RequestBody): Result<BaseResponse>
}