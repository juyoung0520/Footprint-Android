package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentCourseBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseFilterRVAdapter
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.main.course.Filtering.filterState
import com.footprint.footprint.ui.main.course.Filtering.filters
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.CourseViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.logging.Filter

class CourseFragment() : BaseFragment<FragmentCourseBinding>(FragmentCourseBinding::inflate) {

    private var mode: Int = 0   // 모드: 지도(0), 리스트(1)

    private lateinit var mapFragment: CourseMapFragment
    private lateinit var listFragment: CourseListFragment

    private val courseVm: CourseViewModel by sharedViewModel()
    private lateinit var filterRVAdapter: CourseFilterRVAdapter

    override fun initAfterBinding() {
        setFragmentSetting()
        setFilterSetting()
        setClickEvent()

        observe()
    }

    private fun setClickEvent(){
        // 코스 검색으로 이동
        binding.courseSearchIv.setOnClickListener {
            val searchWord = binding.courseSearchBarEt.text
            val cameraPosition = (mapFragment as CourseMapFragment).getCameraPosition()

            if(searchWord.isNotEmpty()){
                val intent = Intent(requireContext(), CourseSearchActivity::class.java).apply {
                    putExtra("searchWord", searchWord.toString())
                    putExtra("cameraPosition", Gson().toJson(cameraPosition))
                }
                startActivity(intent)
            }
        }

        // 마이 코스로 이동 /* 갤럭시 */
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
        filterRVAdapter = CourseFilterRVAdapter((activity as MainActivity).supportFragmentManager, filters, filterState)
        binding.courseFilterRv.adapter = filterRVAdapter

        // 초기 초기화 버튼 설정
        if (getNumberOfActivateFilters(filterState) >= 1) {
            binding.courseResetIv.isSelected = true
            binding.courseResetTv.isSelected = true
        }else{
            binding.courseResetIv.isSelected = false
            binding.courseResetTv.isSelected = false
        }

        filterRVAdapter.setMyListener(object : CourseFilterRVAdapter.MyListener {
            override fun onChange() {
                courseVm.updateFilteredCourseList()

                // 초기화 버튼 상태 관리
                if (getNumberOfActivateFilters(filterState) >= 1) {
                    binding.courseResetIv.isSelected = true
                    binding.courseResetTv.isSelected = true
                } else {
                    binding.courseResetIv.isSelected = false
                    binding.courseResetTv.isSelected = false
                }
            }

            override fun onModeChange(mode: String) {
                courseVm.getCourses()

                if(mode == SEARCH_IN_MY_LOCATION) // 내 위치 모드
                    mapFragment.setCameraPositionToCurrent()
            }
        })

        // 초기화 버튼 클릭 시,
        binding.courseResetTv.setOnClickListener {
            if (binding.courseResetTv.isSelected) {
                // 필터링 state 리셋
                Filtering.resetFilterState()
                filterRVAdapter.reset(filterState)

                binding.courseResetIv.isSelected = false
                binding.courseResetTv.isSelected = false

                // API 호출
                courseVm.getCourses()
            }
        }
    }

    private fun observe(){
        courseVm.mapBounds.observe(viewLifecycleOwner, Observer {
            // 지도 움직일 때마다 API 호출
            courseVm.getCourses()
        })

        courseVm.filteredCourseList.observe(viewLifecycleOwner, Observer {
            // 필터링된 리스트 바뀔 때마다 UI 바꿔주기
        })


    }
}