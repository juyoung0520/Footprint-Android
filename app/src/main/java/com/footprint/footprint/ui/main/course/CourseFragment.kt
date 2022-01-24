package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.util.Log
import android.view.View
import com.footprint.footprint.databinding.FragmentCourseBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.signin.SplashActivity
import com.footprint.footprint.utils.getLoginStatus
import com.footprint.footprint.utils.removeLoginStatus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.kakao.sdk.user.UserApiClient

class CourseFragment(): BaseFragment<FragmentCourseBinding>(FragmentCourseBinding::inflate) {
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun initAfterBinding() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // email addresses도 요청함
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        //spf에서 로그인 상태 불러오기(kakao, google, null)
        val loginStatus = getLoginStatus(requireContext())
        if(loginStatus == "kakao" || loginStatus == "google"){
            Log.d("AUTO-UNLINK/VALUE", "카카오/구글 로그인되었습니다\n" + "LoginStatus: ${loginStatus}")
            binding.testLogout.visibility = View.VISIBLE
            binding.testUnlink.visibility = View.VISIBLE
        }else if(loginStatus == "null"){
            Log.d("AUTO-UNLINK/NULL", "LoginStatus: ${loginStatus}")
            binding.testLogout.visibility = View.INVISIBLE
            binding.testUnlink.visibility = View.INVISIBLE
        }

        //로그아웃
        binding.testLogout.setOnClickListener {
            if(loginStatus == "kakao"){
                //Kakao Logout
                Log.d("AUTO-LOGOUT/KAKAO", "Kakao 계정에서 로그아웃 하셨습니다.")
                kakaoLogout()
            }else if(loginStatus == "google"){
                //Google Logout
                Log.d("AUTO-LOGOUT/GOOGLE", "Google 계정에서 로그아웃하였습니다.")
                googleLogout()
            }
            removeLoginStatus(requireContext())
            Log.d("Login Status/MYPAGE", getLoginStatus(requireContext()))
        }

        //탈퇴
        binding.testUnlink.setOnClickListener {
            if(loginStatus == "kakao"){
                //Kakao Unlink
                Log.d("AUTO-UNLINK/KAKAO", "Kakao 계정에서 탈퇴하셨습니다.")
                kakaoUnlink()
            }else if(loginStatus == "google"){
                //Google Unlink
                Log.d("AUTO-UNLINK/GOOGLE", "Google 계정에서 탈퇴하셨습니다.")
                googleUnlink()
            }
            removeLoginStatus(requireContext())
            Log.d("Login Status/MYPAGE", getLoginStatus(requireContext()))
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