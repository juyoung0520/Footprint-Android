package com.footprint.footprint.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.footprint.footprint.R
import com.footprint.footprint.config.XAccessTokenInterceptor
import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GlobalApplication: Application() {
    companion object{
        const val X_ACCESS_TOKEN: String = "X-ACCESS-TOKEN"  // JWT Token Key
        const val TAG: String = "FOOTPRINT-APP"              // SharedPreference
        const val REAL_URL:String = "https://mysteps.shop/"
        const val DEV_URL: String = "http://dev.mysteps.shop:3000/"
        const val BASE_URL: String = REAL_URL

        lateinit var retrofit: Retrofit
        lateinit var mSharedPreferences: SharedPreferences
    }


    override fun onCreate() {
        super.onCreate()
        
        KakaoSdk.init(this, getString(R.string.kakao_login_native_key))

        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(30000, TimeUnit.MILLISECONDS)
            .connectTimeout(30000, TimeUnit.MILLISECONDS)
            .addNetworkInterceptor(XAccessTokenInterceptor()) // JWT 자동 헤더 전송
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        mSharedPreferences = applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)
    }
}