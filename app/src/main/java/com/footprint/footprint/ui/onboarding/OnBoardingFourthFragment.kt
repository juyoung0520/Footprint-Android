package com.footprint.footprint.ui.onboarding
import com.footprint.footprint.databinding.FragmentOnboardingFourthBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.autoScrollToBottom

class OnBoardingFourthFragment :
    BaseFragment<FragmentOnboardingFourthBinding>(FragmentOnboardingFourthBinding::inflate) {

    override fun initAfterBinding() {
    }

    override fun onResume() {
        super.onResume()
        autoScrollToBottom(binding.onboardingFourthScrollviewLayout, binding.onboardingFourthImageIv)
    }
}