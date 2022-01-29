package com.footprint.footprint.ui.onboarding

import android.animation.ObjectAnimator
import android.util.Log
import com.footprint.footprint.databinding.FragmentOnboardingThirdBinding
import com.footprint.footprint.ui.BaseFragment

class OnBoardingThirdFragment :
    BaseFragment<FragmentOnboardingThirdBinding>(FragmentOnboardingThirdBinding::inflate) {

    override fun initAfterBinding() {
    }

    override fun onResume() {
        super.onResume()
        binding.onboardingThirdScrollviewLayout.post {
            run {
                ObjectAnimator.ofInt(
                    binding.onboardingThirdScrollviewLayout,
                    "scrollY",
                    binding.onboardingThirdImageIv.bottom
                ).setDuration(1000).start();
            }
        }
    }
}
