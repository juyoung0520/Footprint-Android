package com.footprint.footprint.ui.walk

import com.footprint.footprint.data.remote.footprint.Footprint
import com.footprint.footprint.data.remote.walk.AcquiredBadge
import com.footprint.footprint.data.remote.walk.WalkInfoResponse

interface WalkView {
    fun onWalkLoading()
    fun onWalkFail(code: Int, message: String)
    fun onWriteWalkSuccess(badgeList: List<AcquiredBadge>)
    fun onGetWalkSuccess(walk: WalkInfoResponse)
    fun onGetFootprintsSuccess(footprints: List<Footprint>?)
    fun onDeleteWalkSuccess()
}