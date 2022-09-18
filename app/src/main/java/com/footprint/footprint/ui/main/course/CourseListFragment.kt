package com.footprint.footprint.ui.main.course

import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.FragmentCourseListBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseListRVAdapter
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.viewmodel.CourseViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseListFragment(): BaseFragment<FragmentCourseListBinding>(FragmentCourseListBinding::inflate) {
    private lateinit var networkErrSb: Snackbar

    private val courseVm: CourseViewModel by sharedViewModel()
    private lateinit var courseRVAdapter: CourseListRVAdapter

    override fun initAfterBinding() {
        initRV()
    }

    private fun initRV(){
        courseRVAdapter = CourseListRVAdapter(requireContext())
        binding.courseListRv.adapter = courseRVAdapter

        courseRVAdapter.setMyClickListener(object : CourseListRVAdapter.CourseClickListener{

            // 코스 상세보기로 이동
            override fun onClick(course: CourseDTO, position: Int) {
                val courseJson = Gson().toJson(course)
                val action = CourseFragmentDirections.actionCourseFragmentToCourseDetailActivity(courseJson)
                findNavController().navigate(action)
            }

            // 찜하기 API 호출
            override fun markCourse(courseIdx: Int) {
                courseVm.markCourse(courseIdx)
            }
        })

        observe()
    }

    private fun observe() {
        courseVm.mutableErrorType.observe(requireActivity(), Observer {
            when(it){
                ErrorType.NETWORK -> {

                    when(courseVm.getErrorType()){
                        "getCourses" -> {
                            networkErrSb = Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE)
                                .setAction(getString(R.string.action_retry)){ courseVm.getCourses(null) }
                        }
                        "markCourse" -> {
                            networkErrSb = Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                        }
                    }

                    networkErrSb.show()
                }
                else -> {
                    startErrorActivity("CourseListFragment")
                }
            }
        })

        courseVm.filteredCourseList.observe(requireActivity(), Observer {
            if(::courseRVAdapter.isInitialized)
                courseRVAdapter.addAll(it as List<CourseDTO>)
        })

        courseVm.isMarked.observe(requireActivity(), Observer {
            courseVm.getCourses(null)
        })
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }
}