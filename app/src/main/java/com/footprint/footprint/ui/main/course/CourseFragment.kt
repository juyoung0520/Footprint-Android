package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.os.Bundle
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentCourseBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseFilterRVAdapter
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.main.course.Filtering.filters
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.getNumberOfActivateFilters
import com.footprint.footprint.viewmodel.CourseViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseFragment() : BaseFragment<FragmentCourseBinding>(FragmentCourseBinding::inflate) {
    private var mode: Int = 0   // 모드: 지도(0), 리스트(1)

    private lateinit var mapFragment: CourseMapFragment
    private lateinit var listFragment: CourseListFragment

    private val courseVm: CourseViewModel by sharedViewModel()

    override fun initAfterBinding() {
        setFragmentSetting()
        setFilterSetting()
        setClickEvent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseVm.getCourseList(this)
    }

    private fun setClickEvent(){
        // 코스 검색으로 이동
        binding.courseSearchIv.setOnClickListener {
            startActivity(Intent(requireContext(), CourseSearchActivity::class.java))
        }

        // 마이 코스로 이동
        binding.courseMyCourseIv.setOnClickListener {

        }
    }

    private fun setFragmentSetting() {
        mapFragment = CourseMapFragment()
        listFragment = CourseListFragment()

        // (default) 지도
        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.course_fragment_container, mapFragment).commit()

        binding.courseModeIv.setOnClickListener {
            when (mode) {
                0 -> { // 지도 -> 리스트로 변경
                    mode = 1
                    binding.courseModeIv.setImageResource(R.drawable.ic_map)
                    (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.course_fragment_container, listFragment).commit()
                }
                1 -> { // 리스트 -> 지도로 변경
                    mode = 0
                    binding.courseModeIv.setImageResource(R.drawable.ic_list)
                    (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.course_fragment_container, mapFragment).commit()
                }
            }
        }
    }

    private fun setFilterSetting() {
        val filterState = courseVm.initFilters()
        val filterRVAdapter = CourseFilterRVAdapter((activity as MainActivity).supportFragmentManager, filters, filterState as HashMap<String, Int?>)
        binding.courseFilterRv.adapter = filterRVAdapter

        filterRVAdapter.setMyListener(object : CourseFilterRVAdapter.MyListener {
            override fun onChange(filterState: HashMap<String, Int?>) {
                courseVm.updateFilteredCourseList(filterState)

                // 초기화 버튼 상태 관리
                LogUtils.d("Course", getNumberOfActivateFilters(filterState).toString())
                if (getNumberOfActivateFilters(filterState) >= 1) {
                    binding.courseResetIv.isSelected = true
                    binding.courseResetTv.isSelected = true
                } else {
                    binding.courseResetIv.isSelected = false
                    binding.courseResetTv.isSelected = false
                }
            }
        })

        binding.courseResetTv.setOnClickListener {
            if (binding.courseResetTv.isSelected) {
                filterRVAdapter.reset(courseVm.initFilters() as HashMap<String, Int?>)
                binding.courseResetIv.isSelected = false
                binding.courseResetTv.isSelected = false
            }
        }
    }
}