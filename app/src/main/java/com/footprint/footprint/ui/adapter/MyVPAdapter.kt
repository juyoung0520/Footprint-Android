package com.footprint.footprint.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.footprint.footprint.ui.main.course.RecommendedCourseFragment
import com.footprint.footprint.ui.main.course.WishListFragment

class MyVPAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private val fragments: List<Fragment> = listOf(WishListFragment(), RecommendedCourseFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}