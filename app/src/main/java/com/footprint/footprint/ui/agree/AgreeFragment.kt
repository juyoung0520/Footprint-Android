package com.footprint.footprint.ui.agree

import android.view.View
import android.widget.CheckBox
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentAgreeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.register.RegisterActivity
import com.footprint.footprint.ui.walk.WalkActivity
import com.footprint.footprint.utils.saveNotification

class AgreeFragment : BaseFragment<FragmentAgreeBinding>(FragmentAgreeBinding::inflate){
    private var btnState: Array<Boolean> = arrayOf(false, false, false, false)

    override fun initAfterBinding() {
        setClickListener()

    }

    //클릭 리스너
    private fun setClickListener() {
        /*체크박스*/
        binding.agreeUseCb.setOnClickListener {
            onCheckboxClicked(it)
        }

        binding.agreeLocationCb.setOnClickListener {
            onCheckboxClicked(it)
        }
        binding.agreeUserCb.setOnClickListener {
            onCheckboxClicked(it)
        }
        binding.agreeNotificationCb.setOnClickListener {
            onCheckboxClicked(it)
        }
        binding.agreeAllCheckboxCb.setOnClickListener {
            onCheckboxClicked(it)
        }


        /*더보기 버튼*/
        //이용 약관
        binding.agreeUseIv.setOnClickListener {
            findNavController().navigate(R.id.action_agreeFragment_to_agreeUseFragment)
        }

        //위치서비스 이용약관
        binding.agreeLocationIv.setOnClickListener {
            findNavController().navigate(R.id.action_agreeFragment_to_agreeLocationFragment)
        }

        //개인정보 수집 이용
        binding.agreeUserIv.setOnClickListener {
            findNavController().navigate(R.id.action_agreeFragment_to_agreeUserFragment)
        }

        /*확인 버튼*/
        binding.agreeActionBtn.setOnClickListener{
            //1. 알림 동의 spf 기록
            saveNotification(requireContext(), btnState[3])

            //2. 정보 등록 액티비티로 이동
            val agreeActivity = activity as AgreeActivity
            agreeActivity.startNextActivity(RegisterActivity::class.java)
            agreeActivity.finish()
        }

    }

    private fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                R.id.agree_use_cb -> {  //이용약관
                    btnState[0] = checked
                }
                R.id.agree_location_cb -> { //위치서비스 이용약관
                    btnState[1] = checked
                }
                R.id.agree_user_cb -> { //개인정보 수집 이용
                    btnState[2] = checked
                }
                R.id.agree_notification_cb -> { //앱 푸시 알림 수신 동의
                    btnState[3] = checked
                }
                R.id.agree_all_checkbox_cb -> { //모두 동의합니다
                    agreeAll(checked)
                }
            }
            checkAllChecked()
        }
    }

    private fun agreeAll(agree: Boolean){
        //모든 버튼 체크하기 & 기록
        binding.agreeUseCb.isChecked = agree
        binding.agreeLocationCb.isChecked = agree
        binding.agreeUserCb.isChecked = agree
        binding.agreeNotificationCb.isChecked = agree

        for (i in 0..3)
            btnState[i] = agree
    }

    private fun checkAllChecked() {
        //체크박스 체크
        for (i in 0..2) {
            if (!btnState[i]) {
                binding.agreeActionBtn.isEnabled = false
                binding.agreeAllCheckboxCb.isChecked = false
                return
            }
        }

        binding.agreeActionBtn.isEnabled = true
    }


}