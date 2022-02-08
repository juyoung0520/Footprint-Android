package com.footprint.footprint.ui.main.mypage

import com.footprint.footprint.data.remote.badge.BadgeInfo
import com.footprint.footprint.data.remote.badge.BadgeResponse

interface BadgeView {
    fun onBadgeLoading()
    fun onBadgeFail(code: Int, message: String)
    fun onGetBadgeSuccess(badgeInfo: BadgeResponse)
    fun onChangeRepresentativeBadge(representativeBadge: BadgeInfo)
}