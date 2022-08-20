package com.footprint.footprint.ui.main.course

import android.graphics.PointF
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
    private val courseMarkers = mutableListOf<Marker>()

    private val courses = listOf(
        TmpCourse(
            listOf(
                LatLng(37.57152, 126.97714),
                LatLng(37.56607, 126.98268),
                LatLng(37.56445, 126.97707),
                LatLng(37.55855, 126.97822)
            )
        ),
        TmpCourse(
            listOf(
                LatLng(37.568307, 126.973107),
                LatLng(37.565711, 126.974782),
                LatLng(37.564822, 126.971021),
            )
        ),
        TmpCourse(
            listOf(
                LatLng(37.568003, 126.9772503),
                LatLng(37.5701573, 126.9772503),
                LatLng(37.5701573, 126.9793745)
            )
        )
    )
    private var currentCourseIdx: Int = 0

    override fun initAfterBinding() {
        setBinding()

        initCourseMap()
    }

    private fun setBinding() {
        binding.courseDetailBackIv.setOnClickListener {
            onBackPressed()
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
        }

        // 여러 코스 마커 생성 비동기 처리
        CoroutineScope(Dispatchers.Main).launch {
            initMap(naverMap)
        }
    }

    private fun initMap(naverMap: NaverMap) {
        val listener = Overlay.OnClickListener { overlay ->
            updateMap(overlay.tag as Int, naverMap)
            true
        }

        for ((idx, course) in courses.withIndex()) {
            val courseMarker =
                getMarker(
                    course.coords.first(),
                    OverlayImage.fromResource(R.drawable.ic_course)
                ).apply {
                    tag = idx
                    alpha = 0.5f
                    onClickListener = listener
                    this.map = naverMap
                }

            courseMarkers.add(courseMarker)
        }

        // 처음 선택된 코스
        currentCourseIdx = 0
        courseMarkers[currentCourseIdx].map = null

        val currentCourse = courses[currentCourseIdx]

        pathOverlay = getPath(this@CourseDetailActivity).apply {
            coords = currentCourse.coords
            map = naverMap
        }

        startMarker = getMarker(
            currentCourse.coords.first(),
            OverlayImage.fromResource(R.drawable.ic_course_start),
            PointF(0.5f, 0.95f)
        )
        startMarker.map = naverMap

        endMarker = getMarker(
            currentCourse.coords.last(),
            OverlayImage.fromResource(R.drawable.ic_course_end),
            PointF(0.5f, 0.95f)
        )
        endMarker.map = naverMap
    }

    private fun updateMap(
        idx: Int,
        naverMap: NaverMap
    ) {
        courseMarkers[currentCourseIdx].map = naverMap
        currentCourseIdx = idx
        courseMarkers[currentCourseIdx].map = null

        val course = courses[currentCourseIdx]

        pathOverlay.apply {
            coords = course.coords
            map = naverMap
        }

        startMarker.apply {
            position = course.coords.first()
            map = naverMap
        }

        endMarker.apply {
            position = course.coords.last()
            map = naverMap
        }
    }
}