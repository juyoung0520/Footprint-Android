package com.footprint.footprint.ui.main.course

import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.ActivityCourseSearchBinding
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.CourseFilterRVAdapter
import com.footprint.footprint.ui.adapter.CourseListRVAdapter
import com.footprint.footprint.ui.main.course.Filtering.filterState
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.CourseViewModel
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.OverlayImage
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseSearchActivity: BaseActivity<ActivityCourseSearchBinding>(ActivityCourseSearchBinding::inflate), OnMapReadyCallback{
    companion object{
        const val CLEARED = 111
    }

    private val courseVm: CourseViewModel by viewModel()

    private lateinit var filterRVAdapter: CourseFilterRVAdapter
    private lateinit var courseRVAdapter: CourseListRVAdapter

    private lateinit var map: NaverMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationOverlay: LocationOverlay
    private lateinit var currentLocation: Location

    override fun initAfterBinding() {
        initMap()
        setSlidingUpPanel()
        setClickListener()
        observe()

        // 처음 들어오면 API 호출
        courseVm.getCourses()
    }

    private fun setClickListener(){
        // 뒤로가기/ 검색창 클릭 시,
        binding.courseSearchBackIv.setOnClickListener {
            val intent = intent.apply {
                putExtra("cameraPosition", Gson().toJson(map.cameraPosition))
            }
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.courseSearchBarTv.setOnClickListener {
            val intent = intent.apply {
                putExtra("cameraPosition", Gson().toJson(map.cameraPosition))
            }
            setResult(RESULT_OK, intent)
            finish()
        }

        // 지우기 버튼 클릭 시,
        binding.courseSearchClearIv.setOnClickListener {
            val intent = intent.apply {
                putExtra("cameraPosition", Gson().toJson(map.cameraPosition))
            }
            setResult(CLEARED, intent)
            finish()
        }

        // 초기화 버튼 클릭 시,
        binding.courseSearchResetTv.setOnClickListener {
            if (binding.courseSearchResetTv.isSelected) {
                // 필터링 state 리셋
                Filtering.resetFilterState()
                filterRVAdapter.reset(filterState)

                binding.courseSearchResetIv.isSelected = false
                binding.courseSearchResetTv.isSelected = false

                // VM 코스 필터링
                courseVm.updateFilteredCourseList()
            }
        }

        // 현재 위치에서 검색 클릭 시,
        binding.courseSearchSearchAgainTv.setOnClickListener {
            binding.courseSearchSearchAgainTv.visibility = View.GONE
            binding.courseSearchSearchAgainIv.visibility = View.GONE

            // 검색 API 호출
            courseVm.getCourses()
        }
    }

    private fun initUI(){
        // 검색어 text 지정, 지도 위치 이동, 필터링 상태 반영
        val searchWord = intent.getStringExtra("searchWord")
        binding.courseSearchBarTv.text = searchWord

        setMap()
        initRV()
    }

    private fun initRV(){
        // 필터링 RV
        filterRVAdapter = CourseFilterRVAdapter(supportFragmentManager, Filtering.filters, filterState)
        binding.courseSearchFilterRv.adapter = filterRVAdapter

        // 초기 초기화 버튼 설정
        if (getNumberOfActivateFilters(filterState) >= 1) {
            binding.courseSearchResetIv.isSelected = true
            binding.courseSearchResetTv.isSelected = true
        }else{
            binding.courseSearchResetIv.isSelected = false
            binding.courseSearchResetTv.isSelected = false
        }

        filterRVAdapter.setMyListener(object : CourseFilterRVAdapter.MyListener {
            override fun onChange() {
                // VM 코스 필터링
                courseVm.updateFilteredCourseList()

                // 초기화 버튼 상태 관리
                if (getNumberOfActivateFilters(filterState) >= 1) {
                    binding.courseSearchResetIv.isSelected = true
                    binding.courseSearchResetTv.isSelected = true
                } else {
                    binding.courseSearchResetIv.isSelected = false
                    binding.courseSearchResetTv.isSelected = false
                }
            }

            override fun onModeChange(mode: String) {
                courseVm.getCourses()

                if(mode == SEARCH_IN_MY_LOCATION && ::currentLocation.isInitialized){
                    moveCamera(CameraPosition(LatLng(currentLocation), map.cameraPosition.zoom))
                }
            }
        })

        // 코스 RV
        courseRVAdapter = CourseListRVAdapter(this)
        binding.courseSearchResultRv.adapter = courseRVAdapter

        courseRVAdapter.setMyClickListener(object : CourseListRVAdapter.CourseClickListener{
            override fun onClick(course: CourseDTO) {
                // 코스 상세보기로 이동
            }

            override fun wishCourse(courseIdx: String) {
                // 찜하기 API 호출
            }
        })

        /* 테스트 */
        dummyData()
    }

    private fun dummyData(){
        val courseDTO = arrayListOf<CourseDTO>().apply {
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
            add(CourseDTO("abc", 127.01, 35.46, "신나는 산책코스", 2.5, 10, 5, 10, listOf("hi", "hello"),"https://i1.sndcdn.com/artworks-OEWgAGpoOqCdgbXC-ghvsbg-t500x500.jpg", true))
        }

        courseRVAdapter.addAll(courseDTO)
    }

    private fun setSlidingUpPanel(){
        val slidingPanel = binding.courseSearchSlidingUpPanelLayout

        val panel = (13 + 28 + 15 + ((105+14)*2)) // 슬라이딩 패널 크기 (상단 드래그 이미지, 코스 2개)
        slidingPanel.panelHeight = convertDpToPx(this, panel)
    }

    override fun onBackPressed() {
        binding.courseSearchSlidingUpPanelLayout.apply {
            if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED || panelState == SlidingUpPanelLayout.PanelState.ANCHORED)   //SlidingUpPanelLayout 이 위로 올라가 있으면 아래로 내리기
                panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            else{
                val intent = intent.apply {
                    putExtra("cameraPosition", Gson().toJson(map.cameraPosition))
                }
                setResult(RESULT_OK, intent)

                super.onBackPressed()
            }
        }
    }

    /* 지도 */
    private fun initMap(){
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.course_search_map_fragment) as MapFragment?
                ?: MapFragment.newInstance().also {
                    supportFragmentManager.beginTransaction().add(R.id.course_search_map_fragment, it)
                        .commit()
                }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap
        map.uiSettings.logoGravity = Gravity.CENTER_VERTICAL

        initUI()
        initMapEvent()
        locationActivate()
    }

    private fun setMap(){
        map.uiSettings.isZoomControlEnabled = false
        map.uiSettings.isCompassEnabled = false

        val cameraPosition = Gson().fromJson(intent.getStringExtra("cameraPosition"), CameraPosition::class.java)
        moveCamera(cameraPosition)

        locationOverlay = map.locationOverlay
        locationOverlay.apply {
            icon = OverlayImage.fromResource(R.drawable.ic_location_overlay)
            anchor = PointF(0.5f, 0.5f)
            subIcon = null
        }
        locationOverlay.isVisible = true

        binding.courseSearchLoadingBgV.visibility = View.GONE
        binding.courseSearchLoadingPb.visibility = View.GONE
    }

    private fun initMapEvent() {
        map.addOnCameraChangeListener { _, _ ->
            val bounds = BoundsModel(
                map.contentBounds.southWest,
                map.contentBounds.southEast,
                map.contentBounds.northWest,
                map.contentBounds.northEast
            )
            courseVm.setMapBounds(bounds)

            binding.courseSearchSearchAgainTv.visibility = View.VISIBLE
            binding.courseSearchSearchAgainIv.visibility = View.VISIBLE
        }
    }

    private fun moveCamera(cameraPosition: CameraPosition){
        map.moveCamera(CameraUpdate.toCameraPosition(cameraPosition))
        map.moveCamera(CameraUpdate.scrollBy(PointF(0F, -(13 + 28 + 15 + ((105+14)*2)).toFloat()))) // 카메라 위치 패널 높이만큼 위로 이동
    }

    /* 현위치 */
    private fun locationActivate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    this,
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

                currentLocation = location

                // 현위치 오버레이
                val position = LatLng(location)
                locationOverlay.apply {
                    this.position = position
                    bearing = location.bearing
                }

                // 현위치 bounds
                val bounds = BoundsModel(
                    map.contentBounds.southWest,
                    map.contentBounds.southEast,
                    map.contentBounds.northWest,
                    map.contentBounds.northEast
                )
                courseVm.setCurrentBounds(bounds)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    /* Observe */
    private fun observe(){
        courseVm.filteredCourseList.observe(this, Observer {
            // 재검색 버튼 지워주기


            // 필터링된 리스트 바뀔 때마다 UI 바꿔주기
        })
    }
}