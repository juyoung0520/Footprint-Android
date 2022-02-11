package com.footprint.footprint.ui.main.calendar

import com.footprint.footprint.data.remote.walk.WalkDateResult

interface SearchResultView {
    fun onSearchResultLoading()
    fun onSearchResultFailure(code: Int, message: String)
    fun onSearchResultSuccess(walkDates: List<WalkDateResult>)
}