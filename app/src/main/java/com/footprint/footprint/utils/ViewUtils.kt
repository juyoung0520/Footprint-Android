package com.footprint.footprint.utils

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

fun getDeiviceWidth(): Int {
    // px 반환
    return Resources.getSystem().displayMetrics.widthPixels
}

fun convertDpToPx(context: Context, dp: Int): Int {
    val density = context.resources.displayMetrics.density
    return Math.round(dp * density)
}

fun convertPxToDp(context: Context, px: Int): Int {
    val density = context.resources.displayMetrics.density
    return Math.round(px / density)
}