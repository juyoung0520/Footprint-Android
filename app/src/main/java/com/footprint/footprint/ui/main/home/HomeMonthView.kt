package com.footprint.footprint.ui.main.home

import com.footprint.footprint.data.remote.achieve.TMonth

interface HomeMonthView {
    /*월별 목표 받아오기*/
    fun onTMonthSuccess(tMonth: TMonth)
    fun onTMonthFailure(code: Int, message: String)
}