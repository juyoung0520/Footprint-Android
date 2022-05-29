package com.footprint.footprint.data.mapper

import com.footprint.footprint.data.dto.FootprintModel
import com.footprint.footprint.data.dto.GetFootprintModel
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

    fun mapperToGetFootprintEntityList(getFootprintModelList: List<GetFootprintModel>): List<GetFootprintEntity> {
        val getFootprintEntityList: ArrayList<GetFootprintEntity> = arrayListOf()

        for (getFootprintModel in getFootprintModelList) {
            getFootprintEntityList.add(GetFootprintEntity(
                footprintIdx = getFootprintModel.footprintIdx,
                recordAt = getFootprintModel.recordAt,
                write = getFootprintModel.write,
                photoList = getFootprintModel.photoList,
                tagList = getFootprintModel.tagList,
                onWalk = getFootprintModel.onWalk
            ))
        }

        return getFootprintEntityList
    }
}