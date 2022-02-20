package com.footprint.footprint.utils

import android.util.Log
import com.footprint.footprint.BuildConfig

class LogUtils {
    companion object {
        fun e(tag: String, msg: String) {
            if (BuildConfig.DEBUG) Log.e(tag, msg)
        }

        fun e(tag: String, msg: String, tr: Throwable?) {
            if (BuildConfig.DEBUG) Log.e(tag, msg, tr)
        }

        fun w(tag: String, msg: String) {
            if (BuildConfig.DEBUG) Log.w(tag, msg)
        }

        fun d(tag: String, msg: String) {
            if (BuildConfig.DEBUG) Log.d(tag, msg)
        }

        fun v(tag: String, msg: String) {
            if (BuildConfig.DEBUG) Log.v(tag, msg)
        }

        fun i(tag: String, msg: String) {
            if (BuildConfig.DEBUG) Log.i(tag, msg)
        }
    }
}