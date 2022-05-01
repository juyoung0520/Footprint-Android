package com.footprint.footprint.data.mapper

import com.footprint.footprint.domain.model.WriteWalkReq
import com.footprint.footprint.ui.walk.model.WalkUIModel

object WalkMapper {
    fun mapperToWriteWalkReq(walkUIModel: WalkUIModel): WriteWalkReq = WriteWalkReq(
        startAt = walkUIModel.startAt,
        endAt = walkUIModel.endAt,
        distance = walkUIModel.distance,
        coordinates = walkUIModel.coordinate,
        calorie = walkUIModel.calorie
    )
}