package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.Weather
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.model.LocationModel

interface WeatherRepository {
    suspend fun getWeather(location: LocationModel): Result<Weather>
}