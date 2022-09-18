package com.footprint.footprint.data.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseHashtagDTO(
    val hashtagIdx: Int? = null,
    val hashtag: String? = null
): Parcelable