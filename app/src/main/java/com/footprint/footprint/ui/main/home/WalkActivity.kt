package com.footprint.footprint.ui.main.home

import android.graphics.Color
import android.view.MenuItem
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.annotation.UiThread
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityWalkBinding
import com.footprint.footprint.ui.BaseActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource

class WalkActivity : BaseActivity<ActivityWalkBinding>(ActivityWalkBinding::inflate), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    // 지도 다루는 인터페이스 요소
    private lateinit var map: NaverMap

    override fun initAfterBinding() {
        binding.walkPlusIv.setOnClickListener {
            binding.walkMiddleIv.setImageResource(R.drawable.btn_play)
        }

        binding.walkCancelTv.setOnClickListener {
            finish()
        }

        binding.walkStopIv.setOnClickListener {
            finish()
        }

        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient("9vdg59un1e")

        // 네이버 지도의 초기 옵션 지정정
       val options = NaverMapOptions()
            .locationButtonEnabled(true)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.walk_map_fragment) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                supportFragmentManager.beginTransaction().add(R.id.walk_map_fragment, it).commit()
            }

        // NaverMap 불러오기 위해 인터페이스 등록
        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                map.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // NaverMap 준비되면 호출됨, NaverMap 속성
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap

        val cameraUpdate = CameraUpdate.zoomTo(17.0)
        naverMap.moveCamera(cameraUpdate)

        // 사용자 인터페이스
        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = true

        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        val locationOverlay = naverMap.locationOverlay

        locationOverlay.circleColor = getColor(R.color.primary_62)
        locationOverlay.circleRadius = 50
        locationOverlay.subIcon = null

//        val marker = Marker()
//        marker.position = locationOverlay.position
//        marker.icon = OverlayImage.fromResource(R.drawable.ic_pin_stroke)
//        marker.map = naverMap

        val path = PathOverlay()
        val pathArray = arrayListOf<LatLng>()
        path.width = 30
        path.color = getColor(R.color.primary_62)
        path.outlineColor = getColor(R.color.primary_62)

        naverMap.addOnOptionChangeListener {
            val mode = naverMap.locationTrackingMode

            if (mode == LocationTrackingMode.NoFollow) {
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
            }
        }

        naverMap.addOnLocationChangeListener { location ->
            if(location.speed >= 0.5) {
                pathArray.add(LatLng(location.latitude, location.longitude))

                if (pathArray.size >= 2) {
                    path.coords = pathArray
                    path.map = naverMap
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}