package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.WeatherDTO
import com.footprint.footprint.domain.model.LocationModel

interface WeatherRepository {
    suspend fun getWeather(location: LocationModel): Result<WeatherDTO>
}