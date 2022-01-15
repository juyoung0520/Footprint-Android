package com.footprint.footprint.ui.main.home

interface WeatherView {
    fun onWeatherLoading()
    fun onWeatherSuccess()
    fun onWeatherFailure()
}