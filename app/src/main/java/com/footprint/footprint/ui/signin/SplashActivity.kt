package com.footprint.footprint.ui.signin

import android.util.Log
import android.widget.Toast
import com.footprint.footprint.databinding.ActivitySplashBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.kakao.sdk.user.UserApiClient

import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun initAfterBinding() {
        //checkKakaoLogin()
        checkGoogleLogin()
//        if(!isKakaoLogin && !isGoogleLogin) {
//            Toast.makeText(this, "새로운 로그인 필요", Toast.LENGTH_SHORT).show()
//            this.startNextActivity(SigninActivity::class.java)
//            finish()
//        }
    }

    private fun checkGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // email addresses도 요청함
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this@SplashActivity, gso)

        //구글 로그인 정보 확인
        val gsa = GoogleSignIn.getLastSignedInAccount(this@SplashActivity)
        if (gsa != null) {
            Toast.makeText(this, "기존 로그인 유지 성공", Toast.LENGTH_SHORT).show()
            this.startNextActivity(MainActivity::class.java)
            Log.d("GOOGLE/AUTO-LOGIN", gsa.idToken.toString())
            Log.d("GOOGLE/AUTO-LOGIN", gsa.displayName.toString())
            Log.d("GOOGLE/AUTO-LOGIN", gsa.email.toString())
            finish()
        } else {
            Toast.makeText(this, "새로운 로그인 필요", Toast.LENGTH_SHORT).show()
            this.startNextActivity(SigninActivity::class.java)
            finish()
        }
    }

    private fun checkKakaoLogin() {
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
                getKakaoUser()
            }

        }
    }

    private fun getKakaoUser() {

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KAKAO/USER-FAIL", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i("KAKAO/USER-SUCCESS", "사용자 정보 요청 성공" + user.toString())
            }
        }
    }
}