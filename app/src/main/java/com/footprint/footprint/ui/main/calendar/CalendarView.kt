package com.footprint.footprint.ui.main.calendar

import com.footprint.footprint.data.remote.walk.DayResult

interface CalendarView {
    fun onCalendarLoading()
    fun onCalendarFailure(code: Int, message: String)
    fun onMonthSuccess(monthResult: List<DayResult>)
}