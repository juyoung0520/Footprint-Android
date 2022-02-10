package com.footprint.footprint.ui.setting

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentSettingBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.signin.SplashActivity
import com.footprint.footprint.utils.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.kakao.sdk.user.UserApiClient

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    private lateinit var actionDialogFragment: ActionDialogFragment
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var loginStatus: String

    override fun initAfterBinding() {
        if (!::actionDialogFragment.isInitialized)
            initActionDialog()

        initLoginStatus()
        setMyEventListener()

        //개인정보처리방침, 이용약관, 위치서비스이용약관 밑줄 표시
        binding.settingPrivacyPolicyTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.settingTermsOfUserTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.settingLocationTermsOfServiceTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    override fun onStart() {
        super.onStart()
        //산책기록 잠금 스위치버튼 <- ON/OFF 상태
        if (getPWDstatus(requireContext()) == "ON") {
            binding.settingLockFootprintSb.isChecked = true
            setPwdSettingVisibility()
        }

        //알림 상태
        binding.settingNotificationSb.isChecked = getNotification(requireContext())
    }

    private fun initLoginStatus() {
        //구글 로그인 준비
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        //spf에서 로그인 상태 불러오기(kakao, google, null)
        loginStatus = getLoginStatus(requireContext())
        if (loginStatus == "kakao" || loginStatus == "google") {
            Log.d("AUTO-UNLINK/VALUE", "카카오/구글 로그인되었습니다\n" + "LoginStatus: ${loginStatus}")
        } else if (loginStatus == "null") {
            Log.d("AUTO-UNLINK/NULL", "LoginStatus: ${loginStatus}")
        }

    }

    private fun initActionDialog() {
        actionDialogFragment = ActionDialogFragment()

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun action1(isAction: Boolean) {   //로그아웃
                if (isAction) { //1. 로그아웃 요청    2. SignInActivity 로 이동  3. 현재 액티비티(MainActivity) 종료
                    findNavController().navigate(R.id.action_settingFragment_to_signinActivity)
                    (requireActivity()).finish()
                }
            }

            override fun action2(isAction: Boolean) {   //회원탈퇴
                if (isAction) { //1. 회원탈퇴 요청    2. SignInActivity 로 이동  3. 현재 액티비티(MainActivity) 종료
                    findNavController().navigate(R.id.action_settingFragment_to_signinActivity)
                    (requireActivity()).finish()
                }
            }

        })
    }

    private fun setMyEventListener() {
        //뒤로가기 이미지뷰 클릭 리스너 -> 프래그먼트 종료
        binding.settingBackIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //내 정보 수정 다음 이미지뷰 클릭 리스너 -> 내 정보 조회 프래그먼트(MyInfoFragment)로 이동
        binding.settingUpdateMyInfoNextIv.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_myInfoFragment)
        }

        //로그아웃 텍스트뷰 클릭 리스너 -> 로그아웃 관련 ActionDialogFragment 띄우기
        binding.settingLogoutTv.setOnClickListener {
            setActionDialogBundle(getString(R.string.msg_logout), getString(R.string.msg_logout_desc), getString(R.string.title_logout))
            actionDialogFragment.show(requireActivity().supportFragmentManager, null)
            actionDialogFragment.setMyDialogCallback(object :
                ActionDialogFragment.MyDialogCallback {
                //로그아웃
                override fun action1(isAction: Boolean) {
                    if (isAction) {
                        if (loginStatus == "kakao") {
                            //Kakao Logout
                            Log.d("AUTO-LOGOUT/KAKAO", "Kakao 계정에서 로그아웃 하셨습니다.")
                            kakaoLogout()
                        } else if (loginStatus == "google") {
                            //Google Logout
                            Log.d("AUTO-LOGOUT/GOOGLE", "Google 계정에서 로그아웃하였습니다.")
                            googleLogout()
                        }
                        removeLoginStatus(requireContext())
                        removeJwt()
                    }
                }
                override fun action2(isAction: Boolean) {}
            })
        }

        //회원탈퇴 텍스트뷰 클릭 리스너 -> 회원탈퇴 관련 ActionDialogFragment 띄우기
        binding.settingWithdrawalTv.setOnClickListener {
            setActionDialogBundle(getString(R.string.msg_withdrawal), getString(R.string.msg_withdrawal_desc), getString(R.string.action_withdrawal))
            actionDialogFragment.show(requireActivity().supportFragmentManager, null)
            actionDialogFragment.setMyDialogCallback(object :
                ActionDialogFragment.MyDialogCallback {
                override fun action1(isAction: Boolean) {}

                //탈퇴
                override fun action2(isAction: Boolean) {
                    if (isAction) {
                        if (loginStatus == "kakao") {
                            //Kakao Unlink
                            Log.d("AUTO-UNLINK/KAKAO", "Kakao 계정에서 탈퇴하셨습니다.")
                            kakaoUnlink()
                        } else if (loginStatus == "google") {
                            //Google Unlink
                            Log.d("AUTO-UNLINK/GOOGLE", "Google 계정에서 탈퇴하셨습니다.")
                            googleUnlink()
                        }
                        removeLoginStatus(requireContext())
                        removeJwt()
                    }
                }

            })
        }

        //알림 스위치버튼 클릭 리스너
        binding.settingNotificationSb.setOnClickListener {
            saveNotification(requireContext(), binding.settingNotificationSb.isChecked)
        }

        //산책기록 잠금 스위치버튼 클릭 리스너
        binding.settingLockFootprintSb.setOnClickListener {
            val pwdStatus = getPWDstatus(requireContext())
            if (pwdStatus == "DEFAULT") {
                //DEFAULT: 암호 X, 암호 설정 액티비티(LockActivity)로 이동/SETTING 암호 설정
                startLockActivity("SETTING")
                binding.settingLockFootprintSb.isChecked = false
            } else {
                //SET, ON, OFF: 암호 변경 visibility 변경
                setPwdSettingVisibility()
            }
        }

        //암호 설정 및 변경 클릭 리스너 -> 암호 변경(LockActivity)로 이동/CHANGE 암호 변경
        binding.settingPasswordSettingNextIv.setOnClickListener {
            startLockActivity("CHANGE")
        }
    }

    private fun setActionDialogBundle(msg: String, desc: String, action: String) {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", msg)
        bundle.putString("desc", desc)
        bundle.putString("action", action)
        actionDialogFragment.arguments = bundle
    }

    /*Function- 암호 */
    //암호 변경 visibility 변경
    private fun setPwdSettingVisibility() {
        lateinit var pwdStatus: String
        if (binding.settingLockFootprintSb.isChecked) {
            binding.settingPasswordSettingIv.visibility = View.VISIBLE
            binding.settingPasswordSettingTv.visibility = View.VISIBLE
            binding.settingPasswordSettingNextIv.visibility = View.VISIBLE
            pwdStatus = "ON"
        } else {
            binding.settingPasswordSettingIv.visibility = View.GONE
            binding.settingPasswordSettingTv.visibility = View.GONE
            binding.settingPasswordSettingNextIv.visibility = View.GONE
            pwdStatus = "OFF"
        }
        savePWDstatus(requireContext(), pwdStatus)
    }

    //암호 설정/변경 액티비티 이동(SETTING: 암호 설정, CHANGE: 암호 변경)
    private fun startLockActivity(mode: String) {
        val action = SettingFragmentDirections.actionSettingFragmentToLockActivity3(mode)
        findNavController().navigate(action)
    }

    /*Function - 로그아웃, 탈퇴*/
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