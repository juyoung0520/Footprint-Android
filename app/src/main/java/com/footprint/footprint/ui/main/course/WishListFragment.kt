package com.footprint.footprint.ui.main.course

import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.FragmentWishListBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseListRVAdapter
import com.footprint.footprint.utils.LogUtils

class WishListFragment : BaseFragment<FragmentWishListBinding>(FragmentWishListBinding::inflate), MyFragment.CoursesListener {
    private lateinit var courseRVAdapter: CourseListRVAdapter

    override fun initAfterBinding() {
        initRVAdapter()
    }

    private fun initRVAdapter() {
        courseRVAdapter = CourseListRVAdapter(requireContext())
        binding.wishListRv.adapter = courseRVAdapter
    }

    override fun observer(courses: List<CourseDTO>) {
        LogUtils.d("courses", courses.toString())
        courseRVAdapter.addAll(courses as ArrayList<CourseDTO>)
    }


}