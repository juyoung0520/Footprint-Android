package com.footprint.footprint.ui.signin

import android.util.Log
import com.footprint.footprint.databinding.ActivitySigninBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User

class SigninActivity : BaseActivity<ActivitySigninBinding>(ActivitySigninBinding::inflate) {

    override fun initAfterBinding() {
        //카카오 로그인
        binding.signinKakaologinBtnLayout.setOnClickListener {
            setKakaoLogin()
        }


        //다음에 로그인 할래요
        binding.signinNologinTv.setOnClickListener{
            this.startNextActivity(MainActivity::class.java)
            finish()
        }


    }




    /*Funtion-Kakao*/
    //로그인
    private fun setKakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KAKAO/API-FAILURE", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("KAKAO/API-SUCCESS", "카카오계정으로 로그인 성공 ${token.accessToken}")
                signupKakao()
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@SigninActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(this@SigninActivity) { token, error ->
                if (error != null) {
                    Log.e("KAKAO/API-FAILURE", "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this@SigninActivity, callback = callback)
                } else if (token != null) {
                    Log.i("KAKAO/API-SUCCESS", "카카오톡으로 로그인 성공 ${token.accessToken}")
                    signupKakao()
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this@SigninActivity, callback = callback)
        }
    }
    private fun getKakaoUser(){

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KAKAO/USER-FAIL", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i("KAKAO/USER-SUCCESS", "사용자 정보 요청 성공" + user.toString())
            }
        }
    }
    private fun signupKakao(){
        //1. User 정보 받아오기
        getKakaoUser()

        //2. 회원가입 api 호출

        //3. 액티비티 이동
        this.startNextActivity(MainActivity::class.java)
        finish()
    }


}