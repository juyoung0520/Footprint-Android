package com.footprint.footprint.ui.onboarding

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentOnboardingFirstBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.convertDpToPx

class OnBoardingFirstFragment : BaseFragment<FragmentOnboardingFirstBinding>(FragmentOnboardingFirstBinding::inflate) {

    override fun initAfterBinding() {
        val imgSize = convertDpToPx(requireContext(), 477)
        Glide.with(this).load(R.raw.walking).override(imgSize).into(binding.onboardingFirstImageIv)
    }

}