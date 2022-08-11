package com.footprint.footprint.ui.main.course

import androidx.lifecycle.Observer
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
    }

    private fun initRV(){
        courseRVAdapter = CourseListRVAdapter()
        binding.courseListRv.adapter = courseRVAdapter

        courseRVAdapter.setMyClickListener(object : CourseListRVAdapter.CourseClickListener{
            override fun onClick(course: CourseDTO) {

            }
        })
    }

    private fun observe() {
        courseVm.filteredCourseList.observe(this, Observer {
            // 리스트에 반영
            //courseRVAdapter.addAll(it.toList() as ArrayList<CourseDTO> )
        })
    }
}