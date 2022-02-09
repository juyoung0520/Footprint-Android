package com.footprint.footprint.ui.walk

import com.footprint.footprint.data.remote.footprint.Footprint
import com.footprint.footprint.data.remote.walk.WalkInfoResponse

interface WalkDetailView {
    fun onWalkDetailLoading()
    fun onWalkDetailFail(code: Int, message: String)
    fun onGetWalkSuccess(walk: WalkInfoResponse)
    fun onGetFootprintsSuccess(footprints: List<Footprint>?)
    fun onDeleteWalkSuccess()
    fun onUpdateFootprintSuccess()
}