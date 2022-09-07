package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowInsetsAnimation
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import okhttp3.internal.checkOffsetAndCount
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseSearchActivity: BaseActivity<ActivityCourseSearchBinding>(ActivityCourseSearchBinding::inflate), OnMapReadyCallback{
    private lateinit var networkErrSb: Snackbar
    companion object{
        const val CLEARED = 111
    }

    private val courseVm: CourseViewModel by viewModel()
    private lateinit var searchWord: String

    private lateinit var filterRVAdapter: CourseFilterRVAdapter
    private lateinit var courseRVAdapter: CourseListRVAdapter

    private lateinit var map: NaverMap
    private val markerList = arrayListOf<Marker>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationOverlay: LocationOverlay
    private lateinit var currentLocation: Location

    override fun initAfterBinding() {
        initMap()
        setSlidingUpPanel()
        setClickListener()
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
            courseVm.getCourses(searchWord)
        }
    }

    private fun initUI(){
        // 검색어 text 지정, 지도 위치 이동, 필터링 상태 반영
        searchWord = intent.getStringExtra("searchWord").toString()
        binding.courseSearchBarTv.text = searchWord

        setMap()
        initRV()
        observe()
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
                courseVm.getCourses(searchWord)

                if(mode == SEARCH_IN_MY_LOCATION && ::currentLocation.isInitialized){
                    moveCamera(CameraPosition(LatLng(currentLocation), map.cameraPosition.zoom))
                }
            }
        })

        // 코스 RV
        courseRVAdapter = CourseListRVAdapter(this)
        binding.courseSearchResultRv.adapter = courseRVAdapter

        courseRVAdapter.setMyClickListener(object : CourseListRVAdapter.CourseClickListener{

            // 코스 상세보기로 이동
            override fun onClick(course: CourseDTO) {
                startDetailActivity(course)
            }

            // 찜하기 API 호출
            override fun markCourse(courseIdx: String) {
                courseVm.markCourse(courseIdx.toInt())
            }
        })
    }

    private fun setSlidingUpPanel(){
        val slidingPanel = binding.courseSearchSlidingUpPanelLayout

        // 슬라이딩 패널 크기 (지도의 40% + marginTop 20dp)
        val mapHeight = getDeviceHeight() - binding.courseSearchTopLayout.height
        val panelHeight = (mapHeight*0.4)-convertDpToPx(this, 20)
        slidingPanel.panelHeight = panelHeight.toInt()
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

    private fun startDetailActivity(course: CourseDTO){
        val intent = Intent(this, CourseDetailActivity::class.java).apply {
            putExtra("course", Gson().toJson(course))
        }

        startActivity(intent)
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
        map.addOnCameraChangeListener { reason, _ ->
            val bounds = BoundsModel(
                north = map.contentBounds.northLatitude,
                south = map.cameraPosition.target.latitude,
                east = map.contentBounds.eastLongitude,
                west = map.contentBounds.westLongitude
            )
            courseVm.setMapBounds(bounds)

            binding.courseSearchSearchAgainTv.visibility = View.VISIBLE
            binding.courseSearchSearchAgainIv.visibility = View.VISIBLE

            if(reason == CameraUpdate.REASON_DEVELOPER) courseVm.getCourses(searchWord)
        }
    }

    private fun moveCamera(cameraPosition: CameraPosition){
        map.moveCamera(CameraUpdate.toCameraPosition(cameraPosition))
        map.moveCamera(CameraUpdate.scrollBy(PointF(0F, -(13 + 28 + 15 + ((105+14)*2)).toFloat()))) // 카메라 위치 패널 높이만큼 위로 이동
    }

    /* 마커 관련 */
    private fun addMarker(courseList: List<CourseDTO>){
        if(courseList.isEmpty())
            return

        for(course in courseList){
            val marker = Marker()
            marker.position = LatLng(course.startLat, course.startLong)
            marker.map = map
            marker.icon = OverlayImage.fromResource(R.drawable.ic_location_pin_start)

            marker.setOnClickListener {
                startDetailActivity(course)

                true
            }

            markerList.add(marker)
        }
    }

    private fun clearMarkers(){
        if(markerList.isEmpty())
            return

        for(marker in markerList){
            marker.map = null
        }
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
                    north = map.contentBounds.northLatitude,
                    south = map.contentBounds.southLatitude,
                    east = map.contentBounds.eastLongitude,
                    west = map.contentBounds.westLongitude
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
        courseVm.mutableErrorType.observe(this, Observer {
            when(it){

                ErrorType.NETWORK -> {
                    when(courseVm.getErrorType()){
                        "getCourses" -> {
                            networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE)
                                .setAction(getString(R.string.action_retry)){ courseVm.getCourses(null) }
                        }
                        "markCourse" -> {
                            networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_LONG)
                        }
                    }

                    networkErrSb.show()
                }
                else -> {
                    startErrorActivity("CourseSearchActivity")
                }
            }
        })

        courseVm.filteredCourseList.observe(this, Observer {
            binding.courseSearchSearchAgainTv.visibility = View.GONE
            binding.courseSearchSearchAgainIv.visibility = View.GONE

            // UI 바꿔주기 (마커, 리스트)
            if(!::map.isInitialized || !::courseRVAdapter.isInitialized) return@Observer

            clearMarkers()
            addMarker(it as List<CourseDTO>)
            courseRVAdapter.addAll(it as List<CourseDTO>)
        })

        courseVm.isMarked.observe(this, Observer {
            courseVm.getCourses(searchWord)
        })
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }
}