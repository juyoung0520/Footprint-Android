package com.footprint.footprint.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseHashtagEntity(
    val hashtagIdx: Int? = null,
    val hashtag: String? = null
): Parcelable