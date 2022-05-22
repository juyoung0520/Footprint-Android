package com.footprint.footprint.ui.main.course

import android.graphics.PointF
import android.graphics.Rect
import android.location.Location
import android.location.LocationManager
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.FragmentCourseMapBinding
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.SEARCH_IN_MAP
import com.footprint.footprint.utils.SEARCH_IN_MY_LOCATION
import com.footprint.footprint.viewmodel.CourseViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseMapFragment() :
    BaseFragment<FragmentCourseMapBinding>(FragmentCourseMapBinding::inflate), OnMapReadyCallback {
    companion object{
        const val PIN_START = "start"
        const val PIN_END = "end"
    }

    private var isCameraInitialized = false
    private var pinMode = MutableLiveData(PIN_START)

    private lateinit var map: NaverMap
    private lateinit var locationOverlay: LocationOverlay
    private val markerList = arrayListOf<Marker>()

    private val courseVm: CourseViewModel by sharedViewModel()

    override fun initAfterBinding() {
        initViewMode()
        initMap()
    }

    private fun initViewMode() {
        // 초기 상태 - 시작점
        binding.courseMapViewStartTv.isSelected = true
        binding.courseMapViewEndTv.isSelected = false

        binding.courseMapViewStartTv.setOnClickListener {
            pinMode.postValue(PIN_START)

            binding.courseMapViewStartTv.isSelected = true
            binding.courseMapViewEndTv.isSelected = false
        }

        binding.courseMapViewEndTv.setOnClickListener {
            pinMode.postValue(PIN_END)

            binding.courseMapViewStartTv.isSelected = false
            binding.courseMapViewEndTv.isSelected = true
        }
    }

    private fun initClickListener() {
        binding.courseMapCurrentLocationIv.setOnClickListener {
            courseVm.currentLocation.value?.let { location ->
                map.moveCamera(CameraUpdate.scrollTo(LatLng(location))) // 카메라 이동
            }
        }
    }

    /* 지도 */
    private fun initMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.course_map_fragment) as MapFragment?
                ?: MapFragment.newInstance().also {
                    childFragmentManager.beginTransaction().add(R.id.walkmap_map_fragment, it)
                        .commit()
                }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap

        setMap()
        initMapEvent()
        initClickListener()
        observe()
    }

    private fun setMap() {
        map.moveCamera(CameraUpdate.zoomTo(17.0))
        map.uiSettings.isZoomControlEnabled = false
        map.uiSettings.isCompassEnabled = false

        locationOverlay = map.locationOverlay
        locationOverlay.apply {
            icon = OverlayImage.fromResource(R.drawable.ic_location_overlay)
            anchor = PointF(0.5f, 0.5f)
            subIcon = null
        }
    }

    private fun initMapEvent() {
        map.addOnCameraChangeListener { reason, _ ->
            when (reason) {
                CameraUpdate.REASON_DEVELOPER -> { // 모드 변경: 내 위치 중심
                    courseVm.setMode(SEARCH_IN_MY_LOCATION)
                    binding.courseMapCurrentLocationIv.alpha = 1F
                }
                CameraUpdate.REASON_GESTURE -> {
                    courseVm.setMode(SEARCH_IN_MAP) // 모드 변경: 지도 중심
                    binding.courseMapCurrentLocationIv.alpha = 0.5F
                }
            }

            val bounds = BoundsModel(
                map.contentBounds.southWest,
                map.contentBounds.southEast,
                map.contentBounds.northWest,
                map.contentBounds.northEast
            )
            courseVm.setBounds(bounds)
        }
    }

    private fun addMarker(courseList: ArrayList<CourseDTO>){
        if(courseList.isEmpty())
            return

        for(course in courseList){
            val marker = Marker()
            marker.position = course.latLng
            marker.map = map
            marker.icon = if(pinMode.value == PIN_START) OverlayImage.fromResource(R.drawable.ic_location_pin_start)
                            else OverlayImage.fromResource(R.drawable.ic_location_pin_end)

            markerList.add(marker)
        }
    }

    private fun updateMarkers(courseList: ArrayList<CourseDTO>){
        if(markerList.isEmpty() || courseList.isEmpty())
            return

        for(i in markerList.indices){
            markerList[i].position = courseList[i].latLng
            markerList[i].icon = if(pinMode.value == PIN_START) OverlayImage.fromResource(R.drawable.ic_location_pin_start)
            else OverlayImage.fromResource(R.drawable.ic_location_pin_end)
        }
    }

    private fun clearMarkers(){
        if(markerList.isEmpty())
            return

        for(marker in markerList){
            marker.map = null
        }
    }

    private fun observe() {
        courseVm.currentLocation.observe(this, Observer { location ->
            if (location != null) {
                val position = LatLng(location)
                locationOverlay.apply {
                    this.position = position
                    bearing = location.bearing
                }

                if (!isCameraInitialized) {
                    binding.courseMapLoadingBgV.visibility = View.GONE
                    binding.courseMapLoadingPb.visibility = View.GONE

                    locationOverlay.isVisible = true
                    map.moveCamera(CameraUpdate.scrollTo(position))

                    isCameraInitialized = true
                }
            }
        })

        courseVm.filteredCourseList.observe(this, Observer {
            // 기존 마커 다 지우기
            // clearMarkers()

            // 정렬된 리스트가 바뀌면 새로운 마커들을 띄워준다
            // addMarkers()
        })

        pinMode.observe(this, Observer{
            showToast(it.toString())

/*            // 1) 리스트 자체가 비었음 -> return
            if(courseVm.filteredCourseList.value.isEmpty())
                return@Observer

            // 로딩 ...
            val courseList = courseVm.filteredCourseList.value.toList()
            if(markerList.isEmpty()){ // marker 없음 -> addMarker
                  //  addMarkers(courseList)
            }else{ // marker 존재함 -> updateMarker
                //  updateMarkers(courseList)
            }*/
        })
    }
}