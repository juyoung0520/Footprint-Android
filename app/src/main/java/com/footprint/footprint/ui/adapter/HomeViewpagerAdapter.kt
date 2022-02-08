package com.footprint.footprint.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class HomeViewpagerAdapter(fragment : Fragment, val fragmentList: ArrayList<Fragment>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}