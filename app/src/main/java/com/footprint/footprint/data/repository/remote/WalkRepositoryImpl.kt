package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.WalkRemoteDataSource
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.DayWalkDTO
import com.footprint.footprint.data.dto.MonthDayDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.mapper.FootprintMapper
import com.footprint.footprint.data.mapper.WalkMapper
import com.footprint.footprint.domain.model.Badge
import com.footprint.footprint.domain.model.Walk
import com.footprint.footprint.domain.model.WriteFootprintReq
import com.footprint.footprint.domain.model.WriteWalkReq
import com.footprint.footprint.domain.repository.WalkRepository
import com.footprint.footprint.ui.walk.model.WalkUIModel
import com.footprint.footprint.utils.FormDataUtils
import com.footprint.footprint.utils.NetworkUtils
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody

class WalkRepositoryImpl(private val dataSource: WalkRemoteDataSource): WalkRepository {
    override suspend fun getWalkByIdx(walkIdx: Int): Result<Walk> {
        return when (val response = dataSource.getWalkByIdx(walkIdx)) {
            is Result.Success -> {
                if (response.value.isSuccess)
                    Result.Success(NetworkUtils.decrypt(response.value.result, Walk::class.java))
                else
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

    override suspend fun writeWalk(walk: WalkUIModel): Result<List<Badge>> {
        val walkReq: WriteWalkReq = WalkMapper.mapperToWriteWalkReq(walk)   //서버에 전달할 산책 정보 객체

        val photosReq: ArrayList<MultipartBody.Part> = arrayListOf()    //서버에 전달할 이미지 리스트
        photosReq.add(FormDataUtils.prepareFilePart("photos", walk.pathImg)!!)    //산책 동선 사진을 이미지 리스트에 추가

        val footprintsReq: ArrayList<WriteFootprintReq> = arrayListOf() //서버에 전달할 발자국 데이터
        for (footprint in walk.footprints) {
            val data: WriteFootprintReq = FootprintMapper.mapperToWriteFootprintReq(footprint)
            footprintsReq.add(data)

            //산책 정보의 photoMatchNumList 데이터 가공(각 발자국 별 저장된 이미지 갯수를 저장)
            if (footprint.photos.isEmpty()) {
                walkReq.photoMatchNumList.add(0)
            } else {
                walkReq.photoMatchNumList.add(footprint.photos.size)

                //이미지를 MultipartBody.Part 객체로 생성
                for (photo in footprint.photos)
                    photosReq.add(FormDataUtils.prepareFilePart("photos", photo)!!)
            }
        }

        val walkFormData = FormDataUtils.getJsonBody(walkReq)!!   //산책 정보를 FormData 로 변환
        val footprintsFormData = FormDataUtils.getJsonBody(footprintsReq)!!    //발자국 정보를 FormData 로 변환

        return when (val response = dataSource.writeWalk(walkFormData, footprintsFormData, photosReq)) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    val itemType = object : TypeToken<List<Badge>>() {}.type
                    Result.Success(NetworkUtils.decrypt(response.value.result, itemType))
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
}