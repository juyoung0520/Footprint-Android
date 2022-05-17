package com.footprint.footprint.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.footprint.footprint.BuildConfig
import com.footprint.footprint.config.XAccessTokenInterceptor
import com.footprint.footprint.di.appModule
import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GlobalApplication: Application() {
    companion object{
        const val X_ACCESS_TOKEN: String = "X-ACCESS-TOKEN"     // JWT Token Key
        const val TAG: String = "FOOTPRINT-APP"                 // SharedPreference
        const val PROD_URL: String = "https://prod.mysteps.shop/"    // 배포용 URI
        const val DEV_URL: String = "https://dev.mysteps.shop/" // 개발 URI
        const val BASE_URL: String = DEV_URL

        lateinit var retrofit: Retrofit
        lateinit var mSharedPreferences: SharedPreferences         //APP 기본 SharedPreference
        lateinit var eSharedPreferences: EncryptedSharedPreferences//암호화된  SharedPreference
    }


    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.kakao_login_native_key)

        startKoin {
            androidContext(this@GlobalApplication)
            modules(appModule)
        }

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


        //앱 sharedPreference
        mSharedPreferences = applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)

        //암호화된 sharedPreference
        val masterkey = MasterKey.Builder(applicationContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        eSharedPreferences = EncryptedSharedPreferences.create(applicationContext, "auth", masterkey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM) as EncryptedSharedPreferences
    }
}