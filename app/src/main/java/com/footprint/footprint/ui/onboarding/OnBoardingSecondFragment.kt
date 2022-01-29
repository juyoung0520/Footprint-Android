package com.footprint.footprint.ui.onboarding

import android.animation.ObjectAnimator
import android.util.Log
import com.footprint.footprint.databinding.FragmentOnboardingSecondBinding
import com.footprint.footprint.ui.BaseFragment

class OnBoardingSecondFragment :
    BaseFragment<FragmentOnboardingSecondBinding>(FragmentOnboardingSecondBinding::inflate) {

    override fun initAfterBinding() {
    }

    override fun onResume() {
        super.onResume()
        binding.onboardingSecondScrollviewLayout.post {
            run {
                ObjectAnimator.ofInt(
                    binding.onboardingSecondScrollviewLayout,
                    "scrollY",
                    binding.onboardingFirstImageIv.bottom
                ).setDuration(1000).start();
            }
        }
    }
}