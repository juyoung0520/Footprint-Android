package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentCourseBinding
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseFilterRVAdapter
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.main.course.Filtering.filterState
import com.footprint.footprint.ui.main.course.Filtering.filters
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.CourseViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.naver.maps.map.CameraPosition
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseFragment() : BaseFragment<FragmentCourseBinding>(FragmentCourseBinding::inflate) {
    private lateinit var networkErrSb: Snackbar
    private var mode: Int = 0   // 모드: 지도(0), 리스트(1)

    private lateinit var mapFragment: CourseMapFragment
    private lateinit var listFragment: CourseListFragment

    private val courseVm: CourseViewModel by sharedViewModel()

    private lateinit var filterRVAdapter: CourseFilterRVAdapter
    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>

    override fun initAfterBinding() {
        setFragmentSetting()
        setFilterSetting()
        setClickEvent()

        observe()
    }

    private fun setClickEvent(){
        // 코스 검색으로 이동
        binding.courseSearchIv.setOnClickListener {
            startSearchActivity()
        }

        binding.courseSearchBarEt.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    startSearchActivity()

                    return true
                }
                return false
            }
        })

        // 마이 코스로 이동
        binding.courseMyCourseIv.setOnClickListener {
            findNavController().navigate(R.id.action_courseFragment_to_myFragment)
        }
    }

    private fun startSearchActivity(){
        val searchWord = binding.courseSearchBarEt.text
        val cameraPosition: CameraPosition? = (mapFragment as CourseMapFragment).getCameraPosition()

        // Validation (1) 지도 초기화 (2) 검색어 입력 (3) 카메라 위치
        if(!mapFragment.isInitialized() || searchWord.isEmpty() || cameraPosition == null) return

        val intent = Intent(requireContext(), CourseSearchActivity::class.java).apply {
            putExtra("searchWord", searchWord.toString())
            putExtra("cameraPosition", Gson().toJson(cameraPosition))
        }
        startActivityForResult.launch(intent)
    }

    private fun setFragmentSetting() {
        mapFragment = CourseMapFragment()
        listFragment = CourseListFragment()

        // (default) 지도
        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.course_fragment_container, mapFragment).commit()

        binding.courseModeIv.setOnClickListener {
            if(!mapFragment.isInitialized()) return@setOnClickListener // 지도 Init 후 프래그먼트 전환 가능

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
                // VM 코스 필터링
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
                courseVm.getCourses(null)

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

                // VM 코스 필터링
                courseVm.updateFilteredCourseList()
            }
        }
    }

    private fun observe(){
        courseVm.mutableErrorType.observe(requireActivity(), Observer {
            when(it){
                ErrorType.NETWORK -> {
                    networkErrSb = Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.action_retry)){ courseVm.getCourses(null) }

                    networkErrSb.show()
                }
                else -> {
                    startErrorActivity("CourseFragment")
                }
            }
        })

        // 지도 움직일 때마다 API 호출
        courseVm.mapBounds.observe(requireActivity(), Observer {
            courseVm.getCourses(null)
        })
    }

    /* StartActivityForResult */
    private fun initActivityResult(){
        startActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->

            // Clear 버튼을 누른 경우,
            if(result.resultCode == CourseSearchActivity.CLEARED){
                binding.courseSearchBarEt.text.clear()
                binding.courseSearchBarEt.requestFocus()
            }

            result.data?.let {
                val cameraPosition = Gson().fromJson(it.getStringExtra("cameraPosition"), CameraPosition::class.java)
                mapFragment.lastCameraPosition = cameraPosition
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityResult()
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }
}