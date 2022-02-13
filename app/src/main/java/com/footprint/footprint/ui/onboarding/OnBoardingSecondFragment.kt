package com.footprint.footprint.ui.onboarding

import com.footprint.footprint.databinding.FragmentOnboardingSecondBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.autoScrollToBottom

class OnBoardingSecondFragment :
    BaseFragment<FragmentOnboardingSecondBinding>(FragmentOnboardingSecondBinding::inflate) {

    override fun initAfterBinding() {
    }

    override fun onResume() {
        super.onResume()
        autoScrollToBottom(binding.onboardingSecondScrollviewLayout, binding.onboardingSecondImageIv)
    }
}