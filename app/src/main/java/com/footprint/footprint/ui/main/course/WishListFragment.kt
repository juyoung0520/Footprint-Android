package com.footprint.footprint.ui.main.course

import android.content.Intent
import androidx.lifecycle.Observer
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.FragmentWishListBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseListRVAdapter
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.viewmodel.RecommendedCourseViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class WishListFragment : BaseFragment<FragmentWishListBinding>(FragmentWishListBinding::inflate), MyFragment.CoursesListener {
    private val vm: RecommendedCourseViewModel by viewModel()

    private var markedCourseIdx: Int = -1

    private lateinit var courseRVAdapter: CourseListRVAdapter
    private lateinit var networkErrSb: Snackbar

    override fun initAfterBinding() {
        initRVAdapter()
        observe()
    }

    private fun initRVAdapter() {
        courseRVAdapter = CourseListRVAdapter(requireContext())
        courseRVAdapter.setMyClickListener(object : CourseListRVAdapter.CourseClickListener {
            override fun onClick(course: CourseDTO, position: Int) {
                val intent = Intent(requireContext(), CourseDetailActivity::class.java)
                intent.putExtra("course", Gson().toJson(course))
                startActivity(intent)
            }

            override fun markCourse(courseIdx: Int) {
                markedCourseIdx = courseIdx
                vm.markCourse(markedCourseIdx)
            }
        })
        binding.wishListRv.adapter = courseRVAdapter
    }

    private fun observe() {
        vm.mutableErrorType.observe(this, Observer {
            when (it) {
                ErrorType.NETWORK -> {  //네트워크 에러
                    Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_LONG)
                    networkErrSb.show()
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    startErrorActivity("WishListFragment")
                }
            }
        })

        vm.markCourseResultCode.observe(viewLifecycleOwner, Observer {
            if (it==1000) {
                courseRVAdapter.deleteCourse(markedCourseIdx)
            } else {
                showToast(getString(R.string.error_api_fail))
            }
        })
    }

    override fun observer(courses: List<CourseDTO>) {
        courseRVAdapter.addAll(courses as ArrayList<CourseDTO>)
    }
}