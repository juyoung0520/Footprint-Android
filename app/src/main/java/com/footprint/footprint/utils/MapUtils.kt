package com.footprint.footprint.utils

import android.content.Context
import android.graphics.PointF
import androidx.core.content.ContextCompat
import com.footprint.footprint.R
import com.footprint.footprint.domain.model.SaveWalkFootprintEntity
import com.footprint.footprint.service.BackgroundWalkService
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import okhttp3.internal.notify

fun getPathBounds(paths: MutableList<MutableList<LatLng>>): LatLngBounds? {
    if (paths.isEmpty()) {
        return null
    }

    var latLngBounds = LatLngBounds.from(paths[0])
    if (paths.size > 1) {
        for (i in 1 until paths.size) {
            latLngBounds = latLngBounds.union(LatLngBounds.from(paths[i]))
        }
    }

    return latLngBounds
}

fun getPath(context: Context, color: Int = R.color.primary): PathOverlay {
    return PathOverlay().apply {
        width = 30
        this.color = ContextCompat.getColor(context, color)
        outlineWidth = 0
    }
}

fun getMarker(
    locationPosition: LatLng,
    image: OverlayImage,
    anchor: PointF = PointF(0.5f, 0.5f)
): Marker {
    return Marker().apply {
        position = locationPosition
        icon = image
        this.anchor = anchor
    }
}

fun getFootPrintMarker(locationPosition: LatLng, footprintCount: Int): Marker {
    return Marker().apply {
        position = locationPosition
        anchor = PointF(0.5f, 0.5f)
        width = 100
        height = 100
        zIndex = 100

        icon = when (footprintCount) {
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
    }
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

fun drawWalkPath(paths: MutableList<MutableList<LatLng>>, context: Context, naverMap: NaverMap) {
    // 경로 마크 찍기
    val startMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_start)
    val midMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_middle_end)
    val endMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_end)

    for (i in paths.indices) {
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

fun checkValidPath(paths: MutableList<MutableList<LatLng>>) {
    val removeIndices = arrayListOf<Int>()

    for (i in paths.indices) {
        when (paths[i].size) {
            0 -> removeIndices.add(i) // remove 바로하면 size 달라져서 에러남
            1 -> paths[i].add(paths[i].last()) // 좌표 한개면 복사
        }
    }

    removeIndices.forEach { paths.removeAt(it) }
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