package com.footprint.footprint.ui.signin

import android.content.Intent
import android.os.Handler
import com.footprint.footprint.data.remote.auth.AuthService
import com.footprint.footprint.data.remote.auth.Login
import com.footprint.footprint.data.remote.badge.BadgeInfo
import com.footprint.footprint.data.remote.badge.BadgeService
import com.footprint.footprint.databinding.ActivitySplashBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.onboarding.OnBoardingActivity
import com.footprint.footprint.utils.getJwt
import com.footprint.footprint.utils.getOnboarding
import com.footprint.footprint.utils.removeJwt
import com.google.gson.Gson

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate),
    SplashView, MonthBadgeView {

    override fun initAfterBinding() {
        //온보딩 화면 O/X => 1.5
        val handler = Handler()
        handler.postDelayed({
            //1. 온보딩 실행 여부 spf에서 받아오기
            if (!getOnboarding()) {
                //2. false -> 온보딩 실행해야 함 -> OnboardingActivity
                startNextActivity(OnBoardingActivity::class.java)
                finish()
            } else {
                autoLogin()
            }
        }, 1500)
    }


    private fun autoLogin() {
        if (getJwt() != null) { // O -> 자동로그인 API 호출
            AuthService.autoLogin(this)
        } else {  // X -> 로그인 액티비티
            startNextActivity(SigninActivity::class.java)
            finish()
        }
    }


    /*자동 로그인 API*/
    override fun onAutoLoginSuccess(result: Login?) {
        if (result != null) {
            when (result.status) {
                "ACTIVE" -> {   // 가입된 회원
                    if (result.checkMonthChanged) { // 첫 접속 -> 뱃지 API 호출
                        BadgeService.getMonthBadge(this)
                    } else { // -> 메인 액티비티
                        startMainActivity()
                    }
                }
                "ONGOING" -> { // 가입이 완료되지 않은 회원 -> 로그인 액티비티
                    startSignInActivity()
                }
            }
        }
    }

    override fun onAutoLoginFailure(code: Int, message: String) {
        when(code){
            2001, 2002, 2003, 2004 -> { // JWT 관련 오류 -> 로그인 액티비티,
                removeJwt()
                startSignInActivity()
            }
        }
    }

    /*뱃지 API*/
    override fun onMonthBadgeSuccess(isBadgeExist: Boolean, monthBadge: BadgeInfo?) {
        val intent = Intent(this, MainActivity::class.java)
        if (isBadgeExist)
            intent.putExtra("badge", Gson().toJson(monthBadge))
        startActivity(intent)
    }

    override fun onMonthBadgeFailure(code: Int, message: String) {
    }

    /*액티비티 이동*/
    //Main Activity
    private fun startMainActivity() {
        startNextActivity(MainActivity::class.java)
        finish()
    }

    //SignIn Activity
    private fun startSignInActivity() {
        startNextActivity(SigninActivity::class.java)
        finish()
    }

}
