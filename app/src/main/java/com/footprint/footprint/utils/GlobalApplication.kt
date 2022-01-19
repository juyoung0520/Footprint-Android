package com.footprint.footprint.utils

import android.app.Application
import com.footprint.footprint.R
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, getString(R.string.kakao_login_native_key))
    }
}