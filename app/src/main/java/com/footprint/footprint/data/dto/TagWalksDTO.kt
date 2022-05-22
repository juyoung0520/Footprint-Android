package com.footprint.footprint.data.dto

import com.google.gson.annotations.SerializedName

data class TagWalksDTO (
    @SerializedName("walkAt")val walkAt: String,
    @SerializedName("walks")val walks: List<DayWalkDTO>,
)