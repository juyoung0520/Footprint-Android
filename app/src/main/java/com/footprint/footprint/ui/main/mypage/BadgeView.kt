package com.footprint.footprint.ui.main.mypage

import com.footprint.footprint.data.remote.badge.BadgeInfo
import com.footprint.footprint.data.remote.badge.BadgeResponse

interface BadgeView {
    fun onBadgeLoading()
    fun onGetBadgeSuccess(badgeInfo: BadgeResponse)
    fun onChangeRepresentativeBadgeSuccess(representativeBadge: BadgeInfo)
    fun onGetBadgeFail(code: Int?)
    fun onChangeRepresentativeBadgeFail(code: Int?, badgeIdx: Int)
}