package com.footprint.footprint.ui.signin

import com.footprint.footprint.data.remote.badge.MonthBadge

interface MonthBadgeView {
    fun onMonthBadgeSuccess(isBadgeExist: Boolean, monthBadge: MonthBadge?)
    fun onMonthBadgeFailure(code: Int, message: String)
}