package com.footprint.footprint.domain.usecase


import com.footprint.footprint.data.dto.Weather
import com.footprint.footprint.domain.model.LocationModel
import com.footprint.footprint.data.model.Result
import com.footprint.footprint.domain.repository.WeatherRepository

class GetWeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(location: LocationModel): Result<Weather> = repository.getWeather(location)
}