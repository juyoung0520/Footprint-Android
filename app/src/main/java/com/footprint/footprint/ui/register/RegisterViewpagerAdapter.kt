package com.footprint.footprint.ui.register

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.footprint.footprint.ui.register.goal.RegisterGoalFragment
import com.footprint.footprint.ui.register.info.RegisterInfoFragment

class RegisterViewpagerAdapter(fragment: RegisterActivity): FragmentStateAdapter(fragment) {

   override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> RegisterInfoFragment()
            else -> RegisterGoalFragment()
        }
    }

}