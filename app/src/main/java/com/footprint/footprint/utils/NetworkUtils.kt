package com.footprint.footprint.utils

import com.footprint.footprint.BuildConfig
import com.google.gson.Gson
import java.lang.reflect.Type

object NetworkUtils {
    fun <T> encrypt(data: T): String {
        val json = Gson().toJson(data)
        LogUtils.d("NetworkUtils", "암호화 전 json: ${json}")
        return AES128(BuildConfig.encrypt_key).encrypt(json)
    }

    fun <T> decrypt(data: String?, className: Class<T>): T {
        return Gson().fromJson(data, className)
    }

    fun <T> decrypt(data: String?, type: Type): T {
        return Gson().fromJson(data, type)
    }
}