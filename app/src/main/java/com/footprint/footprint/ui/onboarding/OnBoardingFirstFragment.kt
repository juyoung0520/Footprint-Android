package com.footprint.footprint.ui.onboarding

import androidx.navigation.fragment.findNavController
import com.footprint.footprint.databinding.FragmentOnboardingFirstBinding
import com.footprint.footprint.ui.BaseFragment

class OnBoardingFirstFragment : BaseFragment<FragmentOnboardingFirstBinding>(FragmentOnboardingFirstBinding::inflate) {

    override fun initAfterBinding() {
    }

    private fun navigateNextFragment() {
        val action = OnBoardingFirstFragmentDirections
            .actionObFirstFragmentToObSecondFragment()
        findNavController().navigate(action)
    }
}