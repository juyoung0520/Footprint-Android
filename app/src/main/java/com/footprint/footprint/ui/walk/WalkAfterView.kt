package com.footprint.footprint.ui.walk

import com.footprint.footprint.data.remote.walk.AcquiredBadge

interface WalkAfterView {
    fun onWalkAfterLoading()
    fun onWalkAfterFail(code: Int, message: String)
    fun onWriteWalkSuccess(badgeList: List<AcquiredBadge>)
}