package com.footprint.footprint.ui.main.mypage

import android.content.Intent
import android.util.Log
import com.footprint.footprint.databinding.FragmentMypageBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.signin.SplashActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.google.android.gms.tasks.OnCompleteListener




class MyPageFragment() : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun initAfterBinding() {
        binding.testKakaoLogout.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null)
                    Log.e("KAKAO/LOGOUT-FAILURE", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                else
                    Log.i("KAKAO/LOGOUT-SUCCESS", "로그아웃 성공. SDK에서 토큰 삭제됨")
                    startActivity(Intent(requireContext(), SplashActivity::class.java))

            }
        }


        binding.testKakaoUnlink.setOnClickListener {
            binding.testKakaoLogout.setOnClickListener {
                UserApiClient.instance.unlink { error ->
                    if (error != null)
                        Log.e("KAKAO/UNLINK-FAILURE", "탈퇴 실패. SDK에서 토큰 삭제됨", error)
                    else
                        Log.i("KAKAO/UNLINK-SUCCESS", "탈퇴 성공. SDK에서 토큰 삭제됨")
                    startActivity(Intent(requireContext(), SplashActivity::class.java))

                }
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // email addresses도 요청함
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.testGoogleLogout.setOnClickListener {
            mGoogleSignInClient.signOut()
                .addOnCompleteListener(OnCompleteListener<Void?> {
                    Log.i("GOOGLE/LOGOUT-SUCCESS", "로그아웃 성공. SDK에서 토큰 삭제됨")
                    startActivity(Intent(requireContext(), SplashActivity::class.java))
                })
        }

        binding.testGoogleUnlink.setOnClickListener {
            mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(OnCompleteListener<Void?> {
                    Log.i("GOOGLE/UNLINK-SUCCESS", "탈퇴 성공. SDK에서 토큰 삭제됨")
                    startActivity(Intent(requireContext(), SplashActivity::class.java))
                })
        }
    }
}