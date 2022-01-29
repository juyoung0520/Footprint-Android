package com.footprint.footprint.ui.register

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.footprint.footprint.data.model.UserModel
import com.footprint.footprint.ui.register.goal.RegisterGoalFragment

class RegisterViewpagerAdapter(fragment: RegisterActivity) : FragmentStateAdapter(fragment) {

    private val fragments: ArrayList<Fragment> = arrayListOf()

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> fragments[0]
            else -> fragments[1]
        }
    }

    fun setFragments(fragments: ArrayList<Fragment>) {
        this.fragments.addAll(fragments)
    }

    fun sendUserToGoalFrag(user: UserModel) {
        (fragments[1] as RegisterGoalFragment).deliverUser(user)
    }

}