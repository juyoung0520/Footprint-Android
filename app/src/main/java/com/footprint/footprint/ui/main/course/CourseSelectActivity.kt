package com.footprint.footprint.ui.main.course

import android.graphics.Color
import android.widget.Toast
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityCourseSelectBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.utils.LogUtils
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.PathOverlay


class CourseSelectActivity : BaseActivity<ActivityCourseSelectBinding>(ActivityCourseSelectBinding::inflate), OnMapReadyCallback {
    private var startMarkerFlag: Boolean = false
    private var endMarkerFlag: Boolean = false
    private var pathFlag: Boolean = false

    private lateinit var startMarker: Marker
    private lateinit var endMarker: Marker

    override fun initAfterBinding() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        val path = PathOverlay()
        path.coords = listOf(
            LatLng(37.57152, 126.97714),
            LatLng(37.56607, 126.98268),
            LatLng(37.56445, 126.97707),
            LatLng(37.55855, 126.97822)
        )
        path.setOnClickListener {
            LogUtils.d("CourseSelectActivity", "path 클릭")
            pathFlag = true
            return@setOnClickListener false
        }
        path.width = 30
        path.outlineWidth = 0
        path.color = getColor(R.color.primary)
        path.map = naverMap

        startMarker = Marker()
        startMarker.position = path.coords[0]
        startMarker.iconTintColor = Color.YELLOW
        startMarker.onClickListener = Overlay.OnClickListener {
            Toast.makeText(application, "시작 마커 클릭", Toast.LENGTH_SHORT).show()
            endMarkerFlag = false
            startMarkerFlag = !startMarkerFlag

            true
        }
        startMarker.map = naverMap

        endMarker = Marker()
        endMarker.position = path.coords[path.coords.size-1]
        endMarker.iconTintColor = Color.BLACK
        endMarker.onClickListener = Overlay.OnClickListener {
            Toast.makeText(application, "끝 마커 클릭", Toast.LENGTH_SHORT).show()
            startMarkerFlag = false
            endMarkerFlag = !endMarkerFlag

            true
        }
        endMarker.map = naverMap

        naverMap.setOnMapLongClickListener { pointF, latLng ->  }

        naverMap.setOnMapClickListener { point, coord ->
            LogUtils.d("CourseSelectActivity", "map 클릭")
            if (pathFlag) {
                if (startMarkerFlag) {
                    startMarker.position = coord
                    startMarkerFlag = false
                    pathFlag = false
                } else if (endMarkerFlag) {
                    endMarker.position = coord
                    endMarkerFlag = false
                    pathFlag = false
                }
            } else {
                if (startMarkerFlag || endMarkerFlag)
                    showToast("파란색으로 표시된 경로에서 선택해주세요.")
            }
        }
    }
}