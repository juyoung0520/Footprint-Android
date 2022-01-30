package com.footprint.footprint.ui.main.mypage

import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentMypageBinding
import com.footprint.footprint.ui.BaseFragment

class MyPageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {
    override fun initAfterBinding() {
        binding.mypageTmpBadgeTv.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_badgeFragment)
        }
    }
}