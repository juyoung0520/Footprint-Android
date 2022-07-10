package com.footprint.footprint.data.mapper

import com.footprint.footprint.data.dto.FootprintModel
import com.footprint.footprint.data.dto.GetFootprintDTO
import com.footprint.footprint.domain.model.GetFootprintEntity
import com.footprint.footprint.domain.model.SaveWalkFootprintEntity

object FootprintMapper {
    fun mapperToFootprintModel(saveWalkFootprintEntity: SaveWalkFootprintEntity): FootprintModel =
        saveWalkFootprintEntity.run {
            FootprintModel(
                coordinates,
                recordAt,
                write,
                hashtagList,
                photos,
                onWalk
            )
        }

    fun mapperToGetFootprintEntityList(getFootprintDTOList: List<GetFootprintDTO>): List<GetFootprintEntity> {
        val getFootprintEntityList: ArrayList<GetFootprintEntity> = arrayListOf()
        var footprintImgIdx: Int = 0

        for (getFootprintModel in getFootprintDTOList) {
            getFootprintEntityList.add(GetFootprintEntity(
                footprintIdx = getFootprintModel.footprintIdx,
                recordAt = getFootprintModel.recordAt,
                write = getFootprintModel.write,
                photoList = getFootprintModel.photoList,
                tagList = getFootprintModel.tagList,
                onWalk = getFootprintModel.onWalk,
                footprintImgIdx = if (getFootprintModel.onWalk==1) footprintImgIdx++ else null
            ))
        }

        return getFootprintEntityList
    }
}