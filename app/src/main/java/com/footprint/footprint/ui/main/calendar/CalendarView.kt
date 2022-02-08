package com.footprint.footprint.ui.main.calendar

import com.footprint.footprint.data.remote.walk.DayResult
import com.footprint.footprint.data.remote.walk.DayWalkResult

interface CalendarView {
    fun onMonthLoading()
    fun onDayWalkLoading()
    fun onCalendarFailure(code: Int, message: String)
    fun onMonthSuccess(monthResult: List<DayResult>)
    fun onDayWalksSuccess(dayWalkResult: List<DayWalkResult>)
}