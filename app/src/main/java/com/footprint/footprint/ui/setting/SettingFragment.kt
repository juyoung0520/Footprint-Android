package com.footprint.footprint.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentSettingBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.lock.LockActivity
import com.footprint.footprint.utils.getPWDstatus
import com.footprint.footprint.utils.savePWDstatus


class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    private lateinit var actionDialogFragment: ActionDialogFragment

    override fun initAfterBinding() {
        if (!::actionDialogFragment.isInitialized)
            initActionDialog()

        //산책기록 잠금 스위치버튼 <- ON/OFF 상태
        if(getPWDstatus(requireContext()) == "ON") binding.settingLockFootprintSb.isChecked = true
        setMyEventListener()
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
            setActionDialogBundle(getString(R.string.msg_logout))
            actionDialogFragment.show(requireActivity().supportFragmentManager, null)
        }

        //회원탈퇴 텍스트뷰 클릭 리스너 -> 회원탈퇴 관련 ActionDialogFragment 띄우기
        binding.settingWithdrawalTv.setOnClickListener {
            setActionDialogBundle(getString(R.string.msg_withdrawal))
            actionDialogFragment.show(requireActivity().supportFragmentManager, null)
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

    private fun setActionDialogBundle(msg: String) {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", msg)
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
        val intent = Intent(requireContext(), LockActivity::class.java)
        intent.putExtra("mode", mode)
        startActivity(intent)
    }

    /*Function - 로그아웃/탈퇴*/
}