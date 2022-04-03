package com.footprint.footprint.data.mapper

import com.footprint.footprint.domain.model.WriteFootprintReq
import com.footprint.footprint.ui.walk.model.FootprintUIModel

object FootprintMapper {
    fun mapperToWriteFootprintReq(footprintUIModel: FootprintUIModel): WriteFootprintReq = WriteFootprintReq(
        footprintUIModel.coordinate,
        footprintUIModel.recordAt,
        footprintUIModel.write,
        footprintUIModel.hashtagList,
        footprintUIModel.isMarked
    )
}