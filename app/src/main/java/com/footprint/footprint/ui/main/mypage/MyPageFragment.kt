package com.footprint.footprint.ui.main.mypage

import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentMypageBinding
import com.footprint.footprint.ui.BaseFragment

class MyPageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {
    override fun initAfterBinding() {
        setMyClickListener()
    }

    private fun setMyClickListener() {
        //뱃지 텍스트뷰 클릭 리스너 -> BadgeFragment 로 이동(임시)
        binding.mypageTmpBadgeTv.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_badgeFragment)
        }

        //설정 버튼 클릭 리스너 -> SettingFragment 로 이동
        binding.mypageSearchIv.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_navigation)
        }

        binding.mypageTmpGoalTv.setOnClickListener { //이번달 목표 텍스트뷰 클릭 리스너 -> GoalThisMonthFragment 로 이동(임시)
            findNavController().navigate(R.id.action_mypageFragment_to_goalThisMonthFragment)
        }
    }
}