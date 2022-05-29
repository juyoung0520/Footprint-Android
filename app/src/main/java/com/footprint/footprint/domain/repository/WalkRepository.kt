package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.DayWalkDTO
import com.footprint.footprint.data.dto.MonthDayDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.BadgeEntity
import com.footprint.footprint.domain.model.GetWalkEntity
import com.footprint.footprint.domain.model.SaveWalkEntity

interface WalkRepository {
    suspend fun getWalkByIdx(walkIdx: Int): Result<GetWalkEntity>
    suspend fun deleteWalk(walkIdx: Int): Result<BaseResponse>
    suspend fun saveWalk(request: SaveWalkEntity): Result<List<BadgeEntity>>
    suspend fun getMonthWalks(year: Int, month: Int): Result<List<MonthDayDTO>>
    suspend fun getDayWalks(date: String): Result<List<DayWalkDTO>>
    suspend fun getTagWalks(tag: String): Result<List<TagWalksDTO>>
}