package com.footprint.footprint.domain.model

import com.google.gson.annotations.SerializedName
import com.naver.maps.geometry.LatLng

data class BoundsModel(
    @SerializedName("southWest") val southWest: LatLng,
    @SerializedName("southEast") val southEast: LatLng,
    @SerializedName("northWest") val northWest: LatLng,
    @SerializedName("northEast") val northEast: LatLng,
)
