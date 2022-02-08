package com.footprint.footprint.ui.agree

import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentAgreeBinding
import com.footprint.footprint.ui.BaseFragment

class AgreeFragment: BaseFragment<FragmentAgreeBinding>(FragmentAgreeBinding::inflate) {
    override fun initAfterBinding() {

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
    }
}