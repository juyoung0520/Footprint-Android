package com.footprint.footprint.ui.signin

import android.os.Handler
import android.util.Log
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivitySplashBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.onboarding.OnBoardingActivity
import com.footprint.footprint.utils.getJwt
import com.footprint.footprint.utils.getLoginStatus
import com.footprint.footprint.utils.getOnboarding
import com.kakao.sdk.user.UserApiClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var isGoogleLogin = false
    private var isKakaoLogin = false

    override fun initAfterBinding() {
        //온보딩 화면 O/X => 3초 후 실행
        val handler = Handler()
        handler.postDelayed({
            //1. 온보딩 실행 여부 spf에서 받아오기
            var onboardingStatus = getOnboarding(this)
            Log.d("ONBOARDING", onboardingStatus.toString())

            if (!onboardingStatus) {
                //2. false -> 온보딩 실행해야 함 -> OnboardingActivity
                startNextActivity(OnBoardingActivity::class.java)
                finish()
            } else {
                //3. true -> 로그인 상태 확인
                checkGoogleLogin()
                checkKakaoLogin()
            }
        }, 3000)
    }


    /*Google 로그인 체크: 1. 로그인 체크 2. 정보 Log(기능 완성되면 지울 것)*/
    private fun checkGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_server_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this@SplashActivity, gso)

        //구글 로그인 정보 확인
        val gsa = GoogleSignIn.getLastSignedInAccount(this@SplashActivity)
        if (gsa == null) {
            //구글 로그인 X
            isGoogleLogin = false
        } else {
            //구글 로그인 O
            isGoogleLogin = true

            Log.d("GOOGLE/AUTO-LOGIN", gsa.idToken.toString())
            Log.d("GOOGLE/AUTO-LOGIN", gsa.displayName.toString())
            Log.d("GOOGLE/AUTO-LOGIN", gsa.email.toString())

        }
    }

    /*Kakao 로그인 체크: 1. 로그인 체크 2. Log 확인(후에 지울 예정)*/
    private fun checkKakaoLogin() {
        // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                //카카오 로그인 x
                isKakaoLogin = false
            } else if (tokenInfo != null) {
                //카카오 로그인 O
                isKakaoLogin = true

                getKakaoUser()
            }

            autoLogin()
        }
    }

    //Log 확인 위해 회원정보 받아오는 함수
    private fun getKakaoUser() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KAKAO/USER-FAIL", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                isKakaoLogin = true
                Log.i("KAKAO/USER-SUCCESS", "사용자 정보 요청 성공" + user.toString())
            }
        }
    }

    /*자동 로그인
    * 1. google:t, kakao:f, login status: google =>  구글 로그인  & Main
    * 2. google:f, kakao:t, login status: kakao  => 카카오 로그인 & Main
    * 3. google:f, kakao:f    ...  나머지         => 로그인 X     & SignIn
    * */
    private fun autoLogin() {
        val loginStatus = getLoginStatus(this)
        if (!isGoogleLogin && isKakaoLogin && loginStatus == "kakao") {
            //Main Activity (Kakao)
            Log.d("AUTO-LOGIN/VALUE", "Google: ${isGoogleLogin} Kakao: ${isKakaoLogin}")
            Log.d("AUTO-LOGIN/KAKAO", "Kakao 계정으로 로그인하였습니다.")
            startNextActivity(MainActivity::class.java)
            finish()
        } else if (isGoogleLogin && !isKakaoLogin && loginStatus == "google") {
            //Main Activity(Google)
            Log.d("AUTO-LOGIN/VALUE", "Google: ${isGoogleLogin} Kakao: ${isKakaoLogin}")
            Log.d("AUTO-LOGIN/GOOGLE", "Google 계정으로 로그인하였습니다.")
            startNextActivity(MainActivity::class.java)
            finish()
        } else {
            //SignUp Activity
            Log.d("AUTO-LOGIN/VALUE", "Google: ${isGoogleLogin} Kakao: ${isKakaoLogin}")
            Log.d("AUTO-LOGIN/NONE", "로그인 정보가 존재하지 않습니다.")
            startNextActivity(SigninActivity::class.java)
            finish()
        }
        Log.d("AUTO-LOGIN/JWT", getJwt().toString())
    }
}
