package com.footprint.footprint.ui.walk

import com.footprint.footprint.data.remote.footprint.Footprint
import com.footprint.footprint.data.remote.walk.WalkInfoResponse

interface WalkDetailView {
    fun onWalkDetailLoading()
    fun onGetWalkSuccess(walk: WalkInfoResponse)
    fun onGetFootprintsSuccess(footprints: List<Footprint>?)
    fun onDeleteWalkSuccess()
    fun onUpdateFootprintSuccess()
    fun onWalkDetailGETFail(code: Int?, walkIdx: Int)
    fun onWalkDeleteFail(code: Int?, walkIdx: Int)
    fun onFootprintUpdateFail(code: Int?, walkIdx: Int, footprintIdx: Int, footprintMap: HashMap<String, Any>, footprintPhoto: List<String>?)
}