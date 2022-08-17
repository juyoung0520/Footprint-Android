package com.footprint.footprint.domain.usecase


import com.footprint.footprint.domain.model.LocationModel
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.WeatherDTO
import com.footprint.footprint.domain.repository.WeatherRepository

class GetWeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(location: LocationModel): Result<WeatherDTO> = repository.getWeather(location)
}