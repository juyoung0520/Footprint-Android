package com.footprint.footprint.ui.signin

import com.footprint.footprint.data.remote.badge.BadgeInfo

interface MonthBadgeView {
    fun onMonthBadgeSuccess(isBadgeExist: Boolean, monthBadge: BadgeInfo?)
    fun onMonthBadgeFailure(code: Int, message: String)
}
