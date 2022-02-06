package com.footprint.footprint.ui.main.home

import com.footprint.footprint.data.remote.badge.MonthBadge
import com.footprint.footprint.data.remote.users.TMonth
import com.footprint.footprint.data.remote.users.Today
import com.footprint.footprint.data.remote.users.User

interface HomeView {
    /*유저 정보 받아오기*/
    fun onUserSuccess(user: User)
    fun onUserFailure(code: Int, message: String)

    /*일별 목표 받아오기*/
    fun onTodaySuccess(today: Today)
    fun onTodayFailure(code: Int, message: String)

    /*월별 목표 받아오기*/
    fun onTMonthSuccess(tMonth: TMonth)
    fun onTMonthFailure(code: Int, message: String)

    /*이달의 뱃지 받아오기*/
    fun onMonthBadgeSuccess(monthBadge: MonthBadge)
    fun onMonthBadgeFailure(code: Int, message: String)
}