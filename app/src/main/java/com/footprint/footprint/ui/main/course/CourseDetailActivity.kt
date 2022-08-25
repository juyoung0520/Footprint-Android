package com.footprint.footprint.ui.main.course

import android.graphics.PointF
import android.view.Gravity
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityCourseDetailBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.utils.getMarker
import com.footprint.footprint.utils.getPath
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


data class TmpCourse(val coords: List<LatLng>)

class CourseDetailActivity :
    BaseActivity<ActivityCourseDetailBinding>(ActivityCourseDetailBinding::inflate),
    OnMapReadyCallback {
    private lateinit var pathOverlay: PathOverlay
    private lateinit var startMarker: Marker
    private lateinit var endMarker: Marker

    private val course = TmpCourse(
        listOf(
            LatLng(37.57152, 126.97714),
            LatLng(37.56607, 126.98268),
            LatLng(37.56445, 126.97707),
            LatLng(37.55855, 126.97822)
        )
    )

    override fun initAfterBinding() {
        setBinding()

        initCourseMap()
    }

    private fun setBinding() {
        binding.courseDetailBackIv.setOnClickListener {
            onBackPressed()
        }

        binding.courseDetailWalkStartBtn.setOnClickListener {
            // 산책 시작
        }
    }

    private fun initCourseMap() {
        val options = NaverMapOptions()
            .compassEnabled(false)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.course_detail_map_fragment) as MapFragment?
                ?: MapFragment.newInstance(options).also {
                    supportFragmentManager.beginTransaction().add(R.id.walk_detail_map_fragment, it)
                        .commit()
                }

        // 지도 비동기 호출
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        naverMap.uiSettings.apply {
            isCompassEnabled = false
            isZoomControlEnabled = false
            isScaleBarEnabled = false
            logoGravity = Gravity.BOTTOM
            setLogoMargin(40, 0, 0, 80)
        }

        // 오버레이 생성
        CoroutineScope(Dispatchers.Main).launch {
            initOverlay(naverMap)
        }
    }

    private fun initOverlay(naverMap: NaverMap) {
        pathOverlay = getPath(this@CourseDetailActivity).apply {
            coords = course.coords
            map = naverMap
        }

        startMarker = getMarker(
            course.coords.first(),
            OverlayImage.fromResource(R.drawable.ic_course_start),
            PointF(0.5f, 0.95f)
        )
        startMarker.map = naverMap

        endMarker = getMarker(
            course.coords.last(),
            OverlayImage.fromResource(R.drawable.ic_course_end),
            PointF(0.5f, 0.95f)
        )
        endMarker.map = naverMap
    }

}