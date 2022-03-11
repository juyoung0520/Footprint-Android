package com.footprint.footprint.utils

import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

//FormData 로 변환하는 유틸 객체
object FormDataUtils {
    fun getPartMap(value: HashMap<String, Any>): HashMap<String, RequestBody> {
        val partMap: HashMap<String, RequestBody> = HashMap()

        for (v in value)
            partMap[v.key] = v.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        return partMap
    }

    fun getJsonBody(value: Any): RequestBody? {
        return Gson().toJson(value).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    fun prepareFilePart(key: String, fileUri: String): MultipartBody.Part? {
        val file: File = File(fileUri)
        val requestFile: RequestBody = file.asRequestBody("image/jpg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(key, file.name, requestFile)
    }
 }