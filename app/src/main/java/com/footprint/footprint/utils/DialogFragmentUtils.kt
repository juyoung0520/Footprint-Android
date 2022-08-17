package com.footprint.footprint.utils

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

object DialogFragmentUtils {
    //다이얼로그 프래그먼트 리사이징 함수
    fun dialogFragmentResize(
        context: Context,
        dialogFragment: DialogFragment,
        width: Float,
        height: Float
    ) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay

            val size = Point()
            display.getSize(size)

            val window = dialogFragment.dialog?.window
            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)
        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            val window = dialogFragment.dialog?.window
            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }

    //다이얼로그 프래그먼트 리사이징(넓이만) 함수
    fun dialogFragmentResizeWidth(context: Context, dialogFragment: DialogFragment, width: Float) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val window = dialogFragment.dialog?.window
        val params: ViewGroup.LayoutParams? = window?.attributes

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)

            val deviceWidth = size.x
            params?.width = (deviceWidth * width).toInt()
            window?.attributes = params as WindowManager.LayoutParams
        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            params?.width = (rect.width() * width).toInt()
            window?.attributes = params as WindowManager.LayoutParams
        }
    }
}