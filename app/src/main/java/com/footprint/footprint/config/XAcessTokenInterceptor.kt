package com.footprint.footprint.config

import com.footprint.footprint.BuildConfig
import com.footprint.footprint.utils.AES128
import com.footprint.footprint.utils.GlobalApplication.Companion.X_ACCESS_TOKEN
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.getJwt
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject

class XAccessTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()

        val jwtToken: String? = getJwt()

        jwtToken?.let {
            builder.addHeader(X_ACCESS_TOKEN, jwtToken)
        }

        val response = chain.proceed(builder.build())
        val responseJson = response.extractResponseJson()
        LogUtils.d("responseJson", responseJson.toString())

        if (responseJson["isSuccess"].toString().toBoolean()) {
//            val decrypt =
//                AES128(BuildConfig.encrypt_key).decrypt(responseJson[""].toString())
//            responseJson.put("", decrypt)

            return response.newBuilder()
                .body(responseJson.toString().toResponseBody("application/json".toMediaType()))
                .build()
        }

        return response
    }

    private fun Response.extractResponseJson(): JSONObject {
        val jsonString = this.body?.string()
        return try {
            JSONObject(jsonString)
        } catch (exception: Exception) {
            LogUtils.d(
                "VinylaResponseUnboxingInterceptor",
                "서버 응답이 json이 아님 : $jsonString"
            )
            throw Exception()
        }
    }
}
