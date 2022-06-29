package com.footprint.footprint.ui.signin

import android.content.Intent
import android.os.Handler
import androidx.lifecycle.Observer
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivitySplashBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.onboarding.OnBoardingActivity
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.SplashViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate){

    private val splashVm: SplashViewModel by viewModel()

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

        observe()
    }

    private fun autoLogin() {
        LogUtils.d("Splash", getJwt().toString())
        if (getJwt() != null) { // O -> 자동로그인 API 호출
            splashVm.autoLogin()
        } else {  // X -> 로그인 액티비티
            startNextActivity(SigninActivity::class.java)
            finish()
        }
    }

    /*액티비티 이동*/
    //Main Activity
    private fun startMainActivity(badgeCheck: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("badgeCheck", badgeCheck)
        startActivity(intent)
        finish()
    }

    //SignIn Activity
    private fun startSignInActivity() {
        startNextActivity(SigninActivity::class.java)
        finish()
    }

    private fun observe(){
        splashVm.mutableErrorType.observe(this, androidx.lifecycle.Observer {
            when (it) {
                ErrorType.JWT -> { //JWT 관련 에러 발생 시, jwt 지우고 로그인 액티비티로 이동
                    removeJwt()
                    startSignInActivity()
                }
                ErrorType.NETWORK -> {
                    Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(
                        R.string.action_retry) {
                    }.show()
                }
                else -> Snackbar.make(binding.root, getString(R.string.error_api_fail), Snackbar.LENGTH_INDEFINITE).setAction(
                    R.string.action_retry) {
                }.show()
            }
        })

        splashVm.thisLogin.observe(this, Observer {
            when(it.status){
                "ACTIVE" -> {   // 가입된 회원
                    startMainActivity(it.checkMonthChanged)
                }
                "ONGOING" -> { // 가입이 완료되지 않은 회원 -> 로그인 액티비티
                    startSignInActivity()
                }
            }
        })
    }

}
