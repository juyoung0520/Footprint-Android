package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.WalkRemoteDataSource
import com.footprint.footprint.data.dto.*
import com.footprint.footprint.data.mapper.BadgeMapper
import com.footprint.footprint.data.mapper.FootprintMapper
import com.footprint.footprint.data.mapper.WalkMapper
import com.footprint.footprint.domain.model.*
import com.footprint.footprint.domain.repository.WalkRepository
import com.footprint.footprint.utils.NetworkUtils
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class WalkRepositoryImpl(private val dataSource: WalkRemoteDataSource): WalkRepository {
    override suspend fun getWalkByIdx(walkIdx: Int): Result<GetWalkEntity> {
        return when (val response = dataSource.getWalkByIdx(walkIdx)) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    //response 데이터를 복호화
                    val getWalkModel: GetWalkModel = NetworkUtils.decrypt(response.value.result, GetWalkModel::class.java)
                    //GetWalkModel -> GetWalkEntity 로 매핑
                    Result.Success(WalkMapper.mapperToGetWalkEntity(getWalkModel))
                } else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun deleteWalk(walkIdx: Int): Result<BaseResponse> {
        return when (val response = dataSource.deleteWalk(walkIdx)) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    response
                else
                    Result.GenericError(response.value.code, response.value.message)
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun saveWalk(request: SaveWalkEntity): Result<List<BadgeEntity>> {
        //SaveWalkEntity -> SaveWalkModel 로 매핑
        val footprintModelList: ArrayList<FootprintModel> = arrayListOf()
        if (request.saveWalkFootprints.isNotEmpty()) {
            for (footprintEntity in request.saveWalkFootprints) {
                footprintModelList.add(FootprintMapper.mapperToFootprintModel(footprintEntity))
            }
        }
        val saveWalkReqModel: SaveWalkReqModel = WalkMapper.mapperToSaveWalkReqModel(request)

        //saveWalkModel 데이터를 암호화
        val encryptedGoal = NetworkUtils.encrypt(saveWalkReqModel)
        val requestBody: RequestBody = encryptedGoal.toRequestBody("application/json".toMediaTypeOrNull())

        //산책 정보 저장: BaseResponse -> 여기서부터 해야함.
        return when (val response = dataSource.saveWalk(requestBody)) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    //response 데이터를 복호화
                    val itemType = object : TypeToken<List<SaveWalkBadgeResModel>>() {}.type
                    val badges: List<SaveWalkBadgeResModel> = NetworkUtils.decrypt(response.value.result, itemType)
                    //SaveWalkResponse -> BadgeEntity 로 매핑
                    Result.Success(BadgeMapper.mapperToBadgeEntityList(badges))
                } else
                    Result.GenericError(response.value.code, response.value.message)
            }

            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun getMonthWalks(year: Int, month: Int): Result<List<MonthDayDTO>> {
        return when (val response = dataSource.getMonthWalks(year, month)) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    val listType = object : TypeToken<List<MonthDayDTO>>() {}.type
                    Result.Success(NetworkUtils.decrypt(response.value.result, listType))
                } else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun getDayWalks(date: String): Result<List<DayWalkDTO>> {
        return when (val response = dataSource.getDayWalks(date)) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    val listType = object : TypeToken<List<DayWalkDTO>>() {}.type
                    Result.Success(NetworkUtils.decrypt(response.value.result, listType))
                } else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }

    override suspend fun getTagWalks(tag: String): Result<List<TagWalksDTO>> {
        return when (val response = dataSource.getTagWalks(tag)) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    val listType = object : TypeToken<List<TagWalksDTO>>() {}.type
                    Result.Success(NetworkUtils.decrypt(response.value.result, listType))
                } else
                    Result.GenericError(response.value.code, "")
            }
            is Result.NetworkError -> response
            is Result.GenericError -> response
        }
    }
}