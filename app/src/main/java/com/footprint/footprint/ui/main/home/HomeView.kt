package com.footprint.footprint.ui.main.home

import com.footprint.footprint.data.dto.User
import com.footprint.footprint.data.dto.Weather


interface HomeView {
    /*유저 정보 받아오기*/
    fun onUserSuccess(user: User)

    /*날씨 정보 받아오기*/
    fun onWeatherSuccess(weather: Weather)

    fun onHomeFailure(code: Int, message: String)
}