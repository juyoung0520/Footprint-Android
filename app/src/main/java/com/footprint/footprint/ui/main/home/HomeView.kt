package com.footprint.footprint.ui.main.home

import com.footprint.footprint.data.remote.badge.MonthBadge
import com.footprint.footprint.data.remote.user.User

interface HomeView {
    /*유저 정보 받아오기*/
    fun onUserSuccess(user: User)
    fun onUserFailure(code: Int, message: String)

    /*이달의 뱃지 받아오기*/
    fun onMonthBadgeSuccess(monthBadge: MonthBadge)
    fun onMonthBadgeFailure(code: Int, message: String)
}