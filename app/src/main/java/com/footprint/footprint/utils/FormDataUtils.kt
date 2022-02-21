package com.footprint.footprint.utils

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

//FormData 로 변환하는 유틸 객체
object FormDataUtils {
    fun getPartMap(value: HashMap<String, Any>): HashMap<String, RequestBody> {
        val partMap: HashMap<String, RequestBody> = HashMap()

        for (v in value) {
            val requestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), v.value.toString())
            partMap[v.key] = requestBody
        }

        return partMap
    }

    fun getJsonBody(value: Any): RequestBody {
        return MultipartBody.create(MediaType.parse("application/json"), Gson().toJson(value))
    }

    fun prepareFilePart(key: String, fileUri: String): MultipartBody.Part {
        val file: File = File(fileUri)
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
        return MultipartBody.Part.createFormData(key, file.name, requestFile)
    }
 }