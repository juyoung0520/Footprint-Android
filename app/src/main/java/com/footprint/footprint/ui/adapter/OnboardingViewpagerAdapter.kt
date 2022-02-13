package com.footprint.footprint.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.footprint.footprint.ui.onboarding.*

class OnboardingViewpagerAdapter(fragment: OnBoardingActivity): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> OnBoardingFirstFragment()
            1 -> OnBoardingSecondFragment()
            2 -> OnBoardingThirdFragment()
            else -> OnBoardingFourthFragment()
        }
    }

}