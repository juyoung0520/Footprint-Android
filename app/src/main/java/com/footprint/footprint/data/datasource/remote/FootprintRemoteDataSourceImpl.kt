package com.footprint.footprint.data.datasource.remote

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.repository.remote.BaseRepository
import com.footprint.footprint.data.retrofit.FootprintService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FootprintRemoteDataSourceImpl(private val api: FootprintService): BaseRepository(), FootprintRemoteDataSource {
    override suspend fun getFootprintsByWalkIdx(walkIdx: Int): Result<BaseResponse> {
        return safeApiCall { api.getFootprints(walkIdx).body()!! }
    }

    override suspend fun updateFootprint(
        walkIdx: Int,
        footprintIdx: Int,
        data: HashMap<String, RequestBody>?,
        photos: List<MultipartBody.Part>?
    ): Result<BaseResponse> {
        return safeApiCall { api.updateFootprint(walkIdx, footprintIdx, data, photos).body()!! }
    }
}