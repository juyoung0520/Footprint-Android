package com.footprint.footprint.utils

import com.google.gson.Gson
import okhttp3.MediaType
import java.io.File
import okhttp3.RequestBody
import okhttp3.MultipartBody

object FormDataUtils {
    fun getJsonBody(value: Any): RequestBody {
        return MultipartBody.create(MediaType.parse("application/json"), Gson().toJson(value))
    }

    fun prepareFilePart(key: String, fileUri: String): MultipartBody.Part {
        val file: File = File(fileUri)
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
        return MultipartBody.Part.createFormData(key, file.name, requestFile)
    }
 }