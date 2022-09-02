package com.footprint.footprint.ui.main.course

import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.FragmentCourseListBinding
import com.footprint.footprint.domain.model.CourseInfoModel
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseListRVAdapter
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.viewmodel.CourseViewModel
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseListFragment(): BaseFragment<FragmentCourseListBinding>(FragmentCourseListBinding::inflate) {
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
            override fun onClick(course: CourseDTO) {
                val courseJson = Gson().toJson(course)
                val action = CourseFragmentDirections.actionCourseFragmentToCourseDetailActivity(courseJson)
                findNavController().navigate(action)
            }

            // 찜하기 API 호출
            override fun markCourse(courseIdx: String) {
                courseVm.markCourse(courseIdx.toInt())
            }
        })

        observe()
    }

    private fun observe() {
        courseVm.filteredCourseList.observe(requireActivity(), Observer {
            if(::courseRVAdapter.isInitialized)
                courseRVAdapter.addAll(it as List<CourseDTO>)
        })

        courseVm.isMarked.observe(requireActivity(), Observer {
            courseVm.getCourses(null)
        })
    }
}