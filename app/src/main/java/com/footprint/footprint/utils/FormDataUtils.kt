package com.footprint.footprint.utils

import okhttp3.MediaType
import okhttp3.MultipartBody
import java.io.File
import okhttp3.RequestBody

object FormDataUtils {
    fun getBody(key: String, value: Any): MultipartBody.Part {
        return MultipartBody.Part.createFormData(key, value.toString())
    }

    fun getImageBody(key: String, file: File): MultipartBody.Part {
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)

        return MultipartBody.Part.createFormData(key, file.name, requestFile)
    }
}