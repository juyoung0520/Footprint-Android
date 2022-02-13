package com.footprint.footprint.ui.walk

import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.data.remote.badge.BadgeInfo

interface WalkAfterView {
    fun onWalkAfterLoading()
    fun onWalkAfterFail(code: Int?, walk: WalkModel)
    fun onWriteWalkSuccess(badgeList: List<BadgeInfo>)
}