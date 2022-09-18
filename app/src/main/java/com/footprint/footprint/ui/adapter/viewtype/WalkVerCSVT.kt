package com.footprint.footprint.ui.adapter.viewtype

import com.footprint.footprint.domain.model.SelfCourseEntity

data class WalkVerCSVT(
    val type: Int,
    val data: SelfCourseEntity? = null) {

    companion object {
        const val TYPE_A: Int = 0
        const val TYPE_B: Int = 1
    }
}