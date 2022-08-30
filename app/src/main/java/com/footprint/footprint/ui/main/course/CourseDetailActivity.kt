package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.graphics.PointF
import android.view.Gravity
import androidx.navigation.navArgs
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityCourseDetailBinding
import com.footprint.footprint.domain.model.CourseInfoModel
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.CourseTagRVAdapter
import com.footprint.footprint.ui.walk.WalkActivity
import com.footprint.footprint.utils.getMarker
import com.footprint.footprint.utils.getPath
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseDetailActivity :
    BaseActivity<ActivityCourseDetailBinding>(ActivityCourseDetailBinding::inflate),
    OnMapReadyCallback {
    private val args: CourseDetailActivityArgs by navArgs()
    private val course: CourseInfoModel by lazy {
        Gson().fromJson(args.course, CourseInfoModel::class.java)
    }

    override fun initAfterBinding() {
        setBinding()

        initCourseMap()
    }

    private fun setBinding() {
        binding.courseDetailBackIv.setOnClickListener {
            onBackPressed()
        }

        val tagRVAdapter = CourseTagRVAdapter(course.tags)
        binding.courseDetailTagRv.adapter = tagRVAdapter

        // 찜하기 버튼 관련
        binding.courseDetailLikeIv.isSelected = false
        binding.courseDetailLikeIv.setOnClickListener {
            // courseIDX 가지고 찜하기 버튼 API 호출
            binding.courseDetailLikeIv.isSelected = !binding.courseDetailLikeIv.isSelected
        }

        binding.courseDetailWalkStartBtn.setOnClickListener {
            val intent = Intent(this, WalkActivity::class.java)
            intent.putExtra("course", Gson().toJson(course))
            // 유저 정보도 필요
            val tmpUser = SimpleUserModel(weight = 0, height = 0, goalWalkTime = 30, walkNumber = 1)
            intent.putExtra("userInfo", Gson().toJson(tmpUser))

            startActivity(intent)
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
        course.coords.forEach {
            getPath(this@CourseDetailActivity).apply {
                coords = it
                map = naverMap
            }
        }

        val startMarker = getMarker(
            course.coords.first().first(),
            OverlayImage.fromResource(R.drawable.ic_course_start),
            PointF(0.5f, 0.95f)
        )
        startMarker.map = naverMap

        val endMarker = getMarker(
            course.coords.last().last(),
            OverlayImage.fromResource(R.drawable.ic_course_end),
            PointF(0.5f, 0.95f)
        )
        endMarker.map = naverMap
    }

}