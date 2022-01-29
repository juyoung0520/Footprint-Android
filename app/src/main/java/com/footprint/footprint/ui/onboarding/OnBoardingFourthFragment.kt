package com.footprint.footprint.ui.onboarding

import android.animation.ObjectAnimator
import android.util.Log
import com.footprint.footprint.databinding.FragmentOnboardingFourthBinding
import com.footprint.footprint.ui.BaseFragment

class OnBoardingFourthFragment :
    BaseFragment<FragmentOnboardingFourthBinding>(FragmentOnboardingFourthBinding::inflate) {

    override fun initAfterBinding() {
    }

    override fun onResume() {
        super.onResume()
        binding.onboardingFourthScrollviewLayour.post {
            run {
                ObjectAnimator.ofInt(
                    binding.onboardingFourthScrollviewLayour,
                    "scrollY",
                    binding.onboardingFourthImageIv.bottom
                ).setDuration(1000).start();
            }
        }
    }
}