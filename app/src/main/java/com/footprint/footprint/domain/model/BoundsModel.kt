package com.footprint.footprint.domain.model

import com.google.gson.annotations.SerializedName
import com.naver.maps.geometry.LatLng

data class BoundsModel(
    @SerializedName("north") val north: Double, // north 위도
    @SerializedName("south") val south: Double, // south 위도
    @SerializedName("west") val west: Double,   // west 경도
    @SerializedName("east") val east: Double,   // east 경도
)
