package com.footprint.footprint.domain.model

data class FilteringModel(
    val type: Int,
    val title: String,
    val contents: ArrayList<String>,
    val units: ArrayList<String>?
)

object FilteringMode {
    const val ORDER_BY = 0
    const val FILTER = 1
}

