package com.footprint.footprint.ui.signin

import com.footprint.footprint.data.remote.badge.MonthBadge

interface BadgeView {
    fun onMonthBadgeSuccess(isBadgeExist: Boolean, monthBadge: MonthBadge?)
    fun onMonthBadgeFailure(code: Int, message: String)
}