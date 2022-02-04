package com.footprint.footprint.ui.onboarding

import com.footprint.footprint.databinding.FragmentOnboardingThirdBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.autoScrollToBottom

class OnBoardingThirdFragment :
    BaseFragment<FragmentOnboardingThirdBinding>(FragmentOnboardingThirdBinding::inflate) {

    override fun initAfterBinding() {
    }

    override fun onResume() {
        super.onResume()
        autoScrollToBottom(binding.onboardingThirdScrollviewLayout, binding.onboardingThirdImageIv)
    }
}
