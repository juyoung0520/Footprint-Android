package com.footprint.footprint.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.ui.main.course.MyFragment
import com.footprint.footprint.ui.main.course.RecommendedCourseFragment
import com.footprint.footprint.ui.main.course.WishListFragment
import com.footprint.footprint.utils.ErrorType

class MyVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments: List<Fragment> = listOf(WishListFragment(), RecommendedCourseFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun callObserver(position: Int, course: List<CourseDTO>) {
        (fragments[position] as MyFragment.CoursesListener).observer(course)
    }
}