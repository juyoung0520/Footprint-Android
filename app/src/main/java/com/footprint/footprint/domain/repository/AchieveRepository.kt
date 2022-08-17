package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.*


interface AchieveRepository {
    suspend fun getToday(): Result<TodayDTO>
    suspend fun getTmonth(): Result<TMonthDTO>
    suspend fun getUserInfo(): Result<UserInfoDTO>
}