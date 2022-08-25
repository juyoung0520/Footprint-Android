package com.footprint.footprint.ui.main.course

import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.FragmentCourseListBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseListRVAdapter
import com.footprint.footprint.viewmodel.CourseViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseListFragment(): BaseFragment<FragmentCourseListBinding>(FragmentCourseListBinding::inflate) {
    private val courseVm: CourseViewModel by sharedViewModel()
    private lateinit var courseRVAdapter: CourseListRVAdapter

    override fun initAfterBinding() {
        observe()
        initRV()
        dummyData()
    }

    private fun initRV(){
        courseRVAdapter = CourseListRVAdapter(requireContext())
        binding.courseListRv.adapter = courseRVAdapter

        courseRVAdapter.setMyClickListener(object : CourseListRVAdapter.CourseClickListener{
            override fun onClick(course: CourseDTO) {
                // 코스 상세보기로 이동
                val action = CourseFragmentDirections.actionCourseFragmentToCourseDetailActivity()
                findNavController().navigate(action)
            }

            override fun wishCourse(courseIdx: String) {
                // 찜하기 API 호출
            }
        })
    }

    private fun observe() {
        courseVm.filteredCourseList.observe(requireActivity(), Observer {
            // 리스트에 반영
            //courseRVAdapter.addAll(it.toList() as ArrayList<CourseDTO> )
        })
    }

    /* 테스트 */
    private fun dummyData(){
        val courseDTO = arrayListOf<CourseDTO>().apply {
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
        }

        courseRVAdapter.addAll(courseDTO)
    }
}