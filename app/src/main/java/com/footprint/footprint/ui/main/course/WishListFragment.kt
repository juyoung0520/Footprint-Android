package com.footprint.footprint.ui.main.course

import com.footprint.footprint.databinding.FragmentWishListBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseRVAdapter

class WishListFragment : BaseFragment<FragmentWishListBinding>(FragmentWishListBinding::inflate) {
    private lateinit var courseRVAdapter: CourseRVAdapter

    override fun initAfterBinding() {
        initRVAdapter()
    }

    private fun initRVAdapter() {
        courseRVAdapter = CourseRVAdapter()
        binding.wishListRv.adapter = courseRVAdapter
    }
}