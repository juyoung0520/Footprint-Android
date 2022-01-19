package com.footprint.footprint.ui.main.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.widget.AdapterViewAnimator

class HomeViewpagerAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> HomeDayFragment()
            else -> HomeMonthFragment()
        }
    }
}