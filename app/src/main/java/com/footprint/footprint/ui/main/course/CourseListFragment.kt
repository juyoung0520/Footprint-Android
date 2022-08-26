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
            override fun onClick(course: CourseDTO) {
//                // 임시 코스
//                val course = CourseInfoModel(
//                    title = "산책 성공",
//                    distance = 11,
//                    time = 30,
//                    description = "오늘 기분 좋은 날!!dsjlfldjsljfksdjjflsjflsjfsjlfjdsklfjlsd kfjdljlsj djlsjfslkfjslfj",
//                    tags = listOf<String>("행복", "힐링"),
//                    coords = listOf(listOf(
//                        LatLng(37.57152, 126.97714),
//                        LatLng(37.56607, 126.98268),
//                        LatLng(37.56445, 126.97707),
//                        LatLng(37.55855, 126.97822)
//                    ))
//                )

                // 코스 상세보기로 이동
                val courseJson = Gson().toJson(course)
                val action = CourseFragmentDirections.actionCourseFragmentToCourseDetailActivity(courseJson)
                findNavController().navigate(action)
            }

            override fun markCourse(courseIdx: String) {
                // 찜하기 API 호출
                //courseVm.markCourse(courseIdx.toInt())
            }
        })

        observe()
    }

    private fun observe() {
        courseVm.filteredCourseList.observe(requireActivity(), Observer {
            if(::courseRVAdapter.isInitialized)
                courseRVAdapter.addAll(it as List<CourseDTO>)
        })

        courseVm.isUpdate.observe(requireActivity(), Observer {
            courseVm.getCourses(null)
            showToast("updated")
        })
    }
}