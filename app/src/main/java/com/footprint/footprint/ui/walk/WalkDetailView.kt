package com.footprint.footprint.ui.walk

interface WalkDetailView {
    fun onDeleteWalkSuccess()
    fun onWalkDeleteFail(code: Int?, walkIdx: Int)
}