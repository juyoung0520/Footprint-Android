package com.footprint.footprint.ui.main.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.annotation.UiThread
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityWalkBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import java.io.ByteArrayOutputStream

class WalkActivity : BaseActivity<ActivityWalkBinding>(ActivityWalkBinding::inflate), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    // 지도 다루는 인터페이스 요소
    private lateinit var map: NaverMap

    private lateinit var walk: Walk

    private var isWalking: Boolean = true
    private var currentTime: Int = 0

    private var totalDistance: Float = 0.0f
    private lateinit var lastLocation: Location

    private var calroieConstant: Double = 0.0

    override fun initAfterBinding() {
        calroieConstant = 0.0525 * 50

        setBinding()

        walk = Walk(1800)
        walk.start()

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

    private fun setBinding() {
        binding.walkProgressBar.isEnabled = false

        binding.walkPlusIv.setOnClickListener {
            setWalkState(false)
        }

        binding.walkMiddleIv.setOnClickListener {
            setWalkState(!isWalking)
        }

        binding.walkCancelTv.setOnClickListener {
    //            val intent = Intent()
    //            map.takeSnapshot { bitmap ->
    //                intent.putExtra("bitmap", bitmap)
    //            }
    //            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.walkStopIv.setOnClickListener {
            finish()
        }
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
        uiSettings.isZoomControlEnabled = false

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

        path.width = 25
        path.color = getColor(R.color.primary_62)
        path.outlineColor = getColor(R.color.primary_62)
//        path.patternImage = OverlayImage.fromResource(R.drawable.ic_footprint_off)
//        path.patternInterval = 50

        naverMap.addOnOptionChangeListener {
            val mode = naverMap.locationTrackingMode

            if (mode == LocationTrackingMode.NoFollow) {
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
                locationOverlay.subIcon = null
            }
        }

        naverMap.addOnLocationChangeListener { location ->
            pathArray.add(LatLng(location.latitude, location.longitude))

            if (pathArray.size >= 2) {
                updateDistanca(location)

                updateCalorie()

                updatePace(location.speed)

                path.coords = pathArray
                path.map = naverMap
            }

            lastLocation = location
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    fun setWalkState(isWalking: Boolean) {
        if (isWalking) {
            binding.walkMiddleIv.setImageResource(R.drawable.btn_pause)
            this.isWalking = true
        } else {
            binding.walkMiddleIv.setImageResource(R.drawable.btn_play)
            this.isWalking = false
        }
    }

    fun updateDistanca(location: Location) {
        totalDistance += location.distanceTo(lastLocation)

        binding.walkDistanceNumberTv.text =
            String.format("%.2f", totalDistance / 1000)
    }

    fun updateCalorie() {
        binding.walkCalorieNumberTv.text =
            (calroieConstant * (currentTime / 60)).toInt().toString()
    }

    fun updatePace(speed: Float) {
        binding.walkPaceNumberTv.text = (speed.toInt() * 90).toString()
    }

    inner class Walk(private val goalTime: Int) : Thread() {
        private val maxProgress = 100

        override fun run() {
            super.run()
            try {
                while (true) {
                    if (isWalking) {
                        sleep(1000)
                        currentTime++

                        if (currentTime < 3600) {
                            runOnUiThread {
                                binding.walkProgressBar.progress = currentTime * maxProgress / goalTime
                                binding.walkWalktimeNumberTv.text =
                                    String.format("%02d:%02d", currentTime / 60, currentTime % 60)
                            }
                        } else {
                            runOnUiThread {
                                binding.walkProgressBar.progress = currentTime * maxProgress / goalTime
                                binding.walkWalktimeNumberTv.text =
                                    String.format("%02d:%02d:%02d", currentTime / 3600, currentTime % 3600 / 60, currentTime % 3600 % 60)
                            }
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("interrupt","쓰레드가 종료되었습니다.")
            }

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        walk.interrupt()
    }
}