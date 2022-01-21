package com.footprint.footprint.ui.main.mypage

import android.content.Intent
import android.util.Log
import android.view.View
import com.footprint.footprint.databinding.FragmentMypageBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.signin.SigninActivity
import com.footprint.footprint.ui.signin.SplashActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.google.android.gms.tasks.OnCompleteListener


class MyPageFragment() : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var isGoogleLogin = false
    private var isKakaoLogin = false

    override fun initAfterBinding() {
        checkGoogleLogin()
        checkKakaoLogin()
    }


    /*상태 확인*/
    private fun checkKakaoLogin() {
        // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                //카카오 로그인 x
                isKakaoLogin = false
            } else if (tokenInfo != null) {
                //카카오 로그인 O
                isKakaoLogin = true
                Log.d("AUTO-LOGIN/VALUE", "Google: ${isGoogleLogin} Kakao: ${isKakaoLogin}")
                Log.d("KAKAO/AUTO-LOGIN", tokenInfo.toString())
            }

            autoLogout()
            autoUnlink()
        }
    }

    /*auto 로그아웃, 탈퇴 */
    private fun checkGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // email addresses도 요청함
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        //구글 로그인 정보 확인
        val gsa = GoogleSignIn.getLastSignedInAccount(requireActivity())
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

    private fun autoLogout() {
        if (!isGoogleLogin && !isKakaoLogin) {
            //Btn 숨기기
            Log.d("AUTO-LOGOUT/VALUE", "Google: ${isGoogleLogin} Kakao: ${isKakaoLogin}")
            binding.testLogout.visibility = View.INVISIBLE
        } else if (isGoogleLogin && isKakaoLogin) {
            Log.d("AUTO-LOGOUT/ERROR", "카카오/구글 로그인되었습니다.")
        }

        binding.testLogout.setOnClickListener {
            if (!isGoogleLogin && isKakaoLogin) {
                //Kakao Logout
                Log.d("AUTO-LOGOUT/KAKAO", "Kakao 계정에서 로그아웃 하셨습니다.")
                kakaoLogout()
            } else if (isGoogleLogin && !isKakaoLogin) {
                //Google Logout
                Log.d("AUTO-LOGOUT/GOOGLE", "Google 계정에서 로그아웃하였습니다.")
                googleLogout()
            }

        }
    }


    private fun autoUnlink(){
        if (!isGoogleLogin && !isKakaoLogin) {
            //Btn 숨기기
            Log.d("AUTO-UNLINK/VALUE", "Google: ${isGoogleLogin} Kakao: ${isKakaoLogin}")
            binding.testUnlink.visibility = View.INVISIBLE
        } else if (isGoogleLogin && isKakaoLogin) {
            Log.d("AUTO-UNLINK/ERROR", "카카오/구글 로그인되었습니다")
        }

        binding.testUnlink.setOnClickListener {
            if (!isGoogleLogin && isKakaoLogin) {
                //Kakao Logout
                Log.d("AUTO-UNLINK/KAKAO", "Kakao 계정에서 탈퇴하셨습니다.")
                kakaoUnlink()
            } else if (isGoogleLogin && !isKakaoLogin) {
                //Google Logout
                Log.d("AUTO-UNLINK/GOOGLE", "Google 계정에서 탈퇴하셨습니다.")
                googleUnlink()
            }

        }
    }
    /*Function*/
    private fun googleUnlink() {
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(OnCompleteListener<Void?> {
                Log.i("GOOGLE/UNLINK-SUCCESS", "탈퇴 성공. SDK에서 토큰 삭제됨")
                startActivity(Intent(requireContext(), SplashActivity::class.java))
            })
    }

    private fun googleLogout() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(OnCompleteListener<Void?> {
                Log.i("GOOGLE/LOGOUT-SUCCESS", "로그아웃 성공. SDK에서 토큰 삭제됨")
                startActivity(Intent(requireContext(), SplashActivity::class.java))
            })
    }

    private fun kakaoUnlink() {
        UserApiClient.instance.unlink { error ->
            if (error != null)
                Log.e("KAKAO/UNLINK-FAILURE", "탈퇴 실패. SDK에서 토큰 삭제됨", error)
            else
                Log.i("KAKAO/UNLINK-SUCCESS", "탈퇴 성공. SDK에서 토큰 삭제됨")
            startActivity(Intent(requireContext(), SplashActivity::class.java))

        }
    }

    private fun kakaoLogout() {
        UserApiClient.instance.logout { error ->
            if (error != null)
                Log.e("KAKAO/LOGOUT-FAILURE", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            else
                Log.i("KAKAO/LOGOUT-SUCCESS", "로그아웃 성공. SDK에서 토큰 삭제됨")
            startActivity(Intent(requireContext(), SplashActivity::class.java))

        }
    }


}