package com.footprint.footprint.ui.main.home

import com.footprint.footprint.data.remote.weather.ITEM
import com.footprint.footprint.data.remote.weather.WeatherResponse

interface WeatherView {
    fun onWeatherSuccess(items: List<ITEM>)
    fun onWeatherFailure(code: Int, message: String)
}