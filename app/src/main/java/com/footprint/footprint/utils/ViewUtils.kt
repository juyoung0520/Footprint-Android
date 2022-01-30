package com.footprint.footprint.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
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

/*Set Height*/
fun View.setHeight(value: Int) {
    val lp = layoutParams
    lp?.let {
        lp.height = value
        layoutParams = lp
    }
}

/*Animation*/
fun fadeIn(view: View) {
    view.apply {
        alpha = 0f
        visibility = View.VISIBLE

        animate()
            .alpha(1f)
            .setDuration(300)
            .setListener(null)
    }
}

fun fadeOut(view: View) {
    view.animate()
        .alpha(0f)
        .setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
            }
        })
}

fun autoScrollToBottom(View: NestedScrollView, imgView: View){
    View.scrollTo(0, 0)
    View.post {
        run {
            ObjectAnimator.ofInt(
                View,
                "scrollY",
                imgView.bottom
            ).setDuration(1000).start();
        }
    }
}