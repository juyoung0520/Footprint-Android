package com.footprint.footprint.domain.model

import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecommendEntity(
    var courseName: String,
    var courseImg: String = "",
    val coordinates: MutableList<MutableList<LatLng>>,
    var hashtags: List<CourseHashtagEntity> = listOf(),
    var address: String = "",
    val length: Double,
    val courseTime: Int,
    val walkIdx: Int,
    var description: String = ""
): Parcelable