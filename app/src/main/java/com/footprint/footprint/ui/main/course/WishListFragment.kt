package com.footprint.footprint.ui.main.course

import android.view.View
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentWishListBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseRVAdapter
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.viewmodel.CourseWishViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class WishListFragment : BaseFragment<FragmentWishListBinding>(FragmentWishListBinding::inflate) {
    private lateinit var courseRVAdapter: CourseRVAdapter
    private val courseWishVM: CourseWishViewModel by viewModel()

    override fun initAfterBinding() {
        observe()
        initRVAdapter()

        courseWishVM.getMarkedCourses()
    }

    private fun initRVAdapter() {
        courseRVAdapter = CourseRVAdapter()
        binding.wishListRv.adapter = courseRVAdapter
    }

    private fun observe() {
        courseWishVM.markedCourses.observe(viewLifecycleOwner) {
            // cousreRV에 아이템 적용
        }

        courseWishVM.mutableErrorType.observe(this) {
            //binding.walkAfterLoadingPb.visibility = View.INVISIBLE

            when (it) {
                ErrorType.NETWORK -> {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_network),
                        Snackbar.LENGTH_INDEFINITE
                    ).show()
                }
                ErrorType.UNKNOWN -> {
                    startErrorActivity("WishListFragment")
                }
            }
        }
    }
}