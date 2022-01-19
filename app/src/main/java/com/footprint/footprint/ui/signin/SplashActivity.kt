package com.footprint.footprint.ui.signin

import android.util.Log
import android.widget.Toast
import com.footprint.footprint.databinding.ActivitySplashBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.kakao.sdk.user.UserApiClient

class SplashActivity: BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun initAfterBinding() {
        checkToken()
    }
    private fun checkToken() {
        // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                // 새롭게 로그인 필요
                Toast.makeText(this, "새로운 로그인 필요", Toast.LENGTH_SHORT).show()
                this.startNextActivity(SigninActivity::class.java)
                finish()
            } else if (tokenInfo != null) {
                // 로그인이 이미 되어있으면
                Toast.makeText(this, "기존 로그인 유지 성공", Toast.LENGTH_SHORT).show()
                this.startNextActivity(MainActivity::class.java)
                finish()
                Log.d("KAKAO/AUTO-LOGIN", tokenInfo.toString())
            }

        }
    }
}