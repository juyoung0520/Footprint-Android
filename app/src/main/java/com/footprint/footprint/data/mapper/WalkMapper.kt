package com.footprint.footprint.data.mapper

import com.footprint.footprint.data.dto.FootprintModel
import com.footprint.footprint.data.dto.GetWalkModel
import com.footprint.footprint.domain.model.SaveWalkEntity
import com.footprint.footprint.data.dto.SaveWalkReqModel
import com.footprint.footprint.data.dto.WalkModel
import com.footprint.footprint.domain.model.GetWalkEntity

object WalkMapper {
    fun mapperToSaveWalkReqModel(saveWalkEntity: SaveWalkEntity): SaveWalkReqModel {
        val walkModel: WalkModel = WalkModel(
            startAt = saveWalkEntity.startAt,
            endAt = saveWalkEntity.endAt,
            distance = saveWalkEntity.distance,
            coordinates = saveWalkEntity.coordinate,
            calorie = saveWalkEntity.calorie,
            thumbnail = saveWalkEntity.pathImg
        )

        val footprintList: ArrayList<FootprintModel> = arrayListOf()
        for (footprintEntity in saveWalkEntity.saveWalkFootprints) {
            footprintList.add(
                FootprintModel(
                    coordinates = footprintEntity.coordinates,
                    recordAt = footprintEntity.recordAt,
                    write = footprintEntity.write,
                    hashtagList = footprintEntity.hashtagList,
                    photos = footprintEntity.photos,
                    onWalk = footprintEntity.onWalk
                )
            )
        }

        return SaveWalkReqModel(walk = walkModel, footprintList = footprintList)
    }

    fun mapperToGetWalkEntity(getWalkModel: GetWalkModel): GetWalkEntity = getWalkModel.run {
        GetWalkEntity(
            walkIdx,
            getWalkTime.date,
            getWalkTime.startAt,
            getWalkTime.endAt,
            getWalkTime.timeString,
            calorie,
            distance,
            footCount,
            pathImageUrl
        )
    }
}