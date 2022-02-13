package com.footprint.footprint.ui.onboarding

import com.bumptech.glide.Glide
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentOnboardingFirstBinding
import com.footprint.footprint.ui.BaseFragment

class OnBoardingFirstFragment : BaseFragment<FragmentOnboardingFirstBinding>(FragmentOnboardingFirstBinding::inflate) {

    override fun initAfterBinding() {
        Glide.with(this).load(R.raw.walking).into(binding.onboardingFirstImageIv)
    }

}