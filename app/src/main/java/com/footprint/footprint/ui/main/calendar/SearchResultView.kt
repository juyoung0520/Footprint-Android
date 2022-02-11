package com.footprint.footprint.ui.main.calendar

import com.footprint.footprint.data.remote.walk.WalkDateResult

interface SearchResultView {
    fun onSearchReaultLoading()
    fun onSearchReaultFailure(code: Int, message: String)
    fun onSearchResultSuccess(walkDates: List<WalkDateResult>)
    fun onDeleteWalkSuccess()
}