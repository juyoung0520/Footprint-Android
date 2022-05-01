package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.Footprint

interface FootprintRepository {
    suspend fun getFootprintsByWalkIdx(walkIdx: Int): Result<List<Footprint>>
    suspend fun updateFootprint(walkIdx: Int, footprintIdx: Int, footprintMap: HashMap<String, Any>, footprintPhoto: List<String>?): Result<BaseResponse>
}