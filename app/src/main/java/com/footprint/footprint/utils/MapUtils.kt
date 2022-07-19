package com.footprint.footprint.utils

import android.content.Context
import android.graphics.PointF
import androidx.core.content.ContextCompat
import com.footprint.footprint.R
import com.footprint.footprint.domain.model.SaveWalkFootprintEntity
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay

fun getPathBounds(paths: MutableList<MutableList<LatLng>>): LatLngBounds? {
    if (paths.isEmpty()) {
        return null
    }

    var latLngBounds = LatLngBounds.from(paths[0])
    if (paths.size > 1) {
        for (index in 1 until paths.size) {
            latLngBounds = latLngBounds.union(LatLngBounds.from(paths[index]))
        }
    }

    return latLngBounds
}

fun getPath(context: Context): PathOverlay {
    val path = PathOverlay()
    path.apply {
        width = 30
        color = ContextCompat.getColor(context, R.color.primary)
        outlineWidth = 0
    }

    return path
}

fun getMarker(locationPosition: LatLng, image: OverlayImage): Marker{
    val marker = Marker()
    marker.position = locationPosition
    marker.anchor = PointF(0.5f, 0.5f)
    marker.icon = image

    return marker
}

fun getFootPrintMarker(locationPosition: LatLng, footprintCount: Int): Marker{
    val marker = Marker()
    marker.position = locationPosition
    marker.anchor = PointF(0.5f, 0.5f)
    marker.width = 100
    marker.height = 100
    marker.zIndex = 100

    marker.icon = when (footprintCount) {
        1 -> OverlayImage.fromResource(R.drawable.ic_foot_print1)
        2 -> OverlayImage.fromResource(R.drawable.ic_foot_print2)
        3 -> OverlayImage.fromResource(R.drawable.ic_foot_print3)
        4 -> OverlayImage.fromResource(R.drawable.ic_foot_print4)
        5 -> OverlayImage.fromResource(R.drawable.ic_foot_print5)
        6 -> OverlayImage.fromResource(R.drawable.ic_foot_print6)
        7 -> OverlayImage.fromResource(R.drawable.ic_foot_print7)
        8 -> OverlayImage.fromResource(R.drawable.ic_foot_print8)
        9 -> OverlayImage.fromResource(R.drawable.ic_foot_print9)
        else -> OverlayImage.fromResource(R.drawable.ic_foot_print9)
    }

    return marker
}

// WalkAfter
fun drawFootprints(
    footprints: ArrayList<SaveWalkFootprintEntity>,
    naverMap: NaverMap
) {
    for (i in footprints.indices) {
        val marker = getFootPrintMarker(footprints[i].coordinates!!, i + 1)
        marker.map = naverMap
    }
}

// WalkDetail
fun drawFootprints(
    footprints: List<List<Double>>,
    naverMap: NaverMap
) {
    for (i in footprints.indices) {
        if (footprints[i].isEmpty()) continue

        val latLng = LatLng(footprints[i][0], footprints[i][1])
        val marker = getFootPrintMarker(latLng, i + 1)
        marker.map = naverMap
    }
}

fun drawWalkPath(paths: MutableList<MutableList<LatLng>>,context: Context, naverMap: NaverMap) {
    // 경로 마크 찍기
    val startMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_start)
    val midMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_middle_end)
    val endMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_end)

    for (i in 0 until paths.size) {
        val pathOverlay = getPath(context)
        pathOverlay.coords = paths[i]
        pathOverlay.map = naverMap

        val startMarker = if (i == 0) {
            getMarker(paths[0][0], startMarkerImage)
        } else {
            getMarker(paths[i][0], midMarkerImage)
        }

        val endMarker = if (i == paths.size - 1) {
            getMarker(paths[i].last(), endMarkerImage)
        } else {
            getMarker(paths[i].last(), midMarkerImage)
        }

        startMarker.map = naverMap
        endMarker.map = naverMap
    }
}

fun moveMapCamera(
    paths: MutableList<MutableList<LatLng>>,
    naverMap: NaverMap
) {
    getPathBounds(paths)?.let {
        naverMap.moveCamera(CameraUpdate.fitBounds(it))
    }
    naverMap.uiSettings.isZoomControlEnabled = false
}