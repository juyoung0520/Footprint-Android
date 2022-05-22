package com.footprint.footprint.ui.main.course

import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentCourseBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.CourseViewModel
import com.footprint.footprint.viewmodel.HomeViewModel
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.LocationOverlay
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseFragment(): BaseFragment<FragmentCourseBinding>(FragmentCourseBinding::inflate) {
    private var mode: Int = 0   // 모드: 지도(0), 리스트(1)
    private var filter: Int = 0 // 필터: off(0), on(1)

    private lateinit var mapFragment: CourseMapFragment
    private lateinit var listFragment: CourseListFragment

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val courseVm: CourseViewModel by sharedViewModel()

    override fun initAfterBinding() {
        setFragmentSetting()
        setFilterSetting()
    }

    private fun setFilterSetting() {
        val widthDp = convertDpToPx(requireContext(),
            convertPxToDp(requireContext(), getDeviceWidth()) - (53 + 20 + 12) // 필터 텍스트뷰(53) + 필터 마진(20) + 기본 마진 (12)
        )
        binding.courseBlankV.setWidth(widthDp)

        binding.courseFilterLayout.setOnClickListener {
            when (filter) {
                0 -> { // 필터 on
                    filter = 1
                    it.alpha = 1F
                    binding.courseFilterIv.setImageResource(R.drawable.ic_filter_on)
                    binding.courseBlankV.visibility = View.GONE

                    binding.courseFilterStateTv.text = getString(R.string.title_filter_on)
                    binding.courseFilterStateTv.setTextColor(resources.getColor(R.color.primary))
                    binding.courseFilterStateTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    binding.courseFilterTv.setTextColor(resources.getColor(R.color.primary))
                }
                1 -> { // 필터 off
                    filter = 0
                    it.alpha = 0.5F
                    binding.courseFilterIv.setImageResource(R.drawable.ic_filter_off)
                    binding.courseBlankV.visibility = View.VISIBLE


                    binding.courseFilterStateTv.text = getString(R.string.title_filter_off)
                    binding.courseFilterStateTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    binding.courseFilterStateTv.setTextColor(resources.getColor(R.color.black_light))
                    binding.courseFilterTv.setTextColor(resources.getColor(R.color.black_light))
                }
            }
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

    /* 위치 */
    private fun locationActivate(){
        // Permission Check 여기 안넣으면 에러
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 2000L // 위치 업데이트 주기
            fastestInterval = 1000L // 가장 빠른 위치 업데이트 주기
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 배터리 소모를 고려하지 않으며 정확도를 최우선으로 고려
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            result.lastLocation.let { location ->
                LogUtils.d("Course", location.toString())
                courseVm.setCurrentLocation(location)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationActivate()

        courseVm.getCourseList(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}