package com.footprint.footprint.config

import com.footprint.footprint.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class MapInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val newRequest = request().newBuilder()
            .addHeader("X-NCP-APIGW-API-KEY-ID", BuildConfig.naver_map_gc_client_id)
            .addHeader("X-NCP-APIGW-API-KEY", BuildConfig.naver_map_gc_client_secret)
            .build()
        proceed(newRequest)
    }
}
