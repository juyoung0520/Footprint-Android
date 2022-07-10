package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.datasource.remote.FootprintRemoteDataSource
import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.GetFootprintDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.mapper.FootprintMapper
import com.footprint.footprint.domain.model.GetFootprintEntity
import com.footprint.footprint.domain.repository.FootprintRepository
import com.footprint.footprint.utils.FormDataUtils
import com.footprint.footprint.utils.NetworkUtils
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class FootprintRepositoryImpl(private val dataSource: FootprintRemoteDataSource): FootprintRepository {
    override suspend fun getFootprintsByWalkIdx(walkIdx: Int): Result<List<GetFootprintEntity>> {
        return when (val response = dataSource.getFootprintsByWalkIdx(walkIdx)) {
            is Result.Success -> {
                if (response.value.isSuccess) {
                    //List<GetFootprintModel>로 복호화
                    val itemType = object : TypeToken<List<GetFootprintDTO>>() {}.type
                    val getFootprints: List<GetFootprintDTO> = NetworkUtils.decrypt(response.value.result, itemType)

                    //List<GetFootprintModel> -> List<GetFootprintEntity> 로 매핑
                    Result.Success(FootprintMapper.mapperToGetFootprintEntityList(getFootprints))
                } else
                    Result.GenericError(response.value.code, response.value.message)
            }

            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }

    override suspend fun updateFootprint(
        walkIdx: Int,
        footprintIdx: Int,
        footprintMap: HashMap<String, Any>,
        footprintPhoto: List<String>?
    ): Result<BaseResponse> {
        var partMap: HashMap<String, RequestBody> = HashMap()   //글이나 태그에 대한 수정이 없을 땐 Empty
        var photos: ArrayList<MultipartBody.Part>? = null   //사진에 대한 수정이 없을 땐 null

        if (footprintMap.isNotEmpty())  //글이나 태그에 대한 수정 정보가 있을 때
            partMap = FormDataUtils.getPartMap(footprintMap)

        if (footprintPhoto!=null && footprintPhoto.isNotEmpty()) {  //사진을 추가할 경우 -> 각 자신을 MultipartBody.Part 객체로 변경
            photos = arrayListOf()
            for (photo in footprintPhoto)
                photos!!.add(FormDataUtils.prepareFilePart("photos", photo)!!)
        } else if (footprintPhoto!=null && footprintPhoto.isEmpty()) {  //사진을 모두 삭제할 경우 -> 빈 파일로 MultipartBody 객체를 생성(key 만 보내고 value 는 빈 리스트)
            photos = arrayListOf()
            val attachmentEmpty = "".toRequestBody("image/jpg".toMediaTypeOrNull())
            photos.add(MultipartBody.Part.createFormData("photos", "", attachmentEmpty))
        }

        return when (val response = dataSource.updateFootprint(walkIdx, footprintIdx, partMap, photos)) {
            is Result.Success ->
                if (response.value.isSuccess)
                    response
                else
                    Result.GenericError(response.value.code, response.value.message)
            is Result.GenericError -> response
            is Result.NetworkError -> response
        }
    }
}