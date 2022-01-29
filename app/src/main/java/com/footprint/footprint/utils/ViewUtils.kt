package com.footprint.footprint.utils

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.math.roundToInt

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

fun getDeviceWidth(): Int {
    // px 반환
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getDeviceHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun convertDpToPx(context: Context, dp: Int): Int {
    val density = context.resources.displayMetrics.density
    return (dp * density).roundToInt()
}

fun convertPxToDp(context: Context, px: Int): Int {
    val density = context.resources.displayMetrics.density
    return (px / density).roundToInt()
}

fun convertDpToSp(context: Context, dp: Int): Int {

    return (convertDpToPx(context, dp) / context.resources.displayMetrics.scaledDensity).toInt()
}

//뷰의 params.height 지정
fun View.setHeight(value: Int) {
    val lp = layoutParams
    lp?.let {
        lp.height = value
        layoutParams = lp
    }
}