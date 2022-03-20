package com.footprint.footprint.data.retrofit

import com.footprint.footprint.data.dto.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface BadgeService {
    //뱃지 조회 API
    @GET("users/badges")
    suspend fun getBadges(): Response<BaseResponse>

    //대표 뱃지 수정 API
    @PATCH("users/badges/title/{badgeIdx}")
    suspend fun changeRepresentativeBadge(@Path("badgeIdx") badgeIdx: Int): Response<BaseResponse>
}