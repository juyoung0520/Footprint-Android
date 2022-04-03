package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface FootprintRemoteDataSource {
    suspend fun getFootprintsByWalkIdx(walkIdx: Int): Result<BaseResponse>
    suspend fun updateFootprint(walkIdx: Int, footprintIdx: Int, data: HashMap<String, RequestBody>?, photos: List<MultipartBody.Part>?): Result<BaseResponse>
}