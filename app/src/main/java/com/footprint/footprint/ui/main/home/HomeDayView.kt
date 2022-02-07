package com.footprint.footprint.ui.main.home

import com.footprint.footprint.data.remote.users.Today

interface HomeDayView {
    /*일별 목표 받아오기*/
    fun onTodaySuccess(today: Today)
    fun onTodayFailure(code: Int, message: String)
}