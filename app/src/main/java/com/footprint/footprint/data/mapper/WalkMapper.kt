package com.footprint.footprint.data.mapper

import com.naver.maps.geometry.LatLng

import com.footprint.footprint.data.dto.FootprintModel
import com.footprint.footprint.data.dto.GetWalkModel
import com.footprint.footprint.domain.model.SaveWalkEntity
import com.footprint.footprint.data.dto.SaveWalkReqModel
import com.footprint.footprint.data.dto.WalkModel
import com.footprint.footprint.data.mapper.FootprintMapper.convertFootprintCoordinates
import com.footprint.footprint.domain.model.GetWalkEntity

object WalkMapper {
    fun mapperToSaveWalkReqModel(saveWalkEntity: SaveWalkEntity): SaveWalkReqModel {
        val walkModel: WalkModel = WalkModel(
            startAt = saveWalkEntity.startAt,
            endAt = saveWalkEntity.endAt,
            distance = saveWalkEntity.distance,
            coordinates = convertToCoordinates(saveWalkEntity.coordinate),
            calorie = saveWalkEntity.calorie,
            thumbnail = saveWalkEntity.pathImg
        )

        val footprintList: ArrayList<FootprintModel> = arrayListOf()
        for (footprintEntity in saveWalkEntity.saveWalkFootprints) {
            footprintList.add(
                FootprintModel(
                    coordinates = convertFootprintCoordinates(footprintEntity.coordinates),
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
            pathImageUrl,
            convertToPaths(coordinate),
            footCoordinates
        )
    }

    fun convertToCoordinates(paths: MutableList<MutableList<LatLng>>): List<List<Double>> {
        val coordinate = arrayListOf<ArrayList<Double>>()

        paths.forEach {
            coordinate.add(arrayListOf())
            it.forEach { lang ->
                coordinate.last().add(lang.latitude)
                coordinate.last().add(lang.longitude)
            }
        }
        return coordinate
    }

    // 나중에 mapper로 이동.
    fun convertToPaths(coordinates: List<List<Double>>): MutableList<MutableList<LatLng>> {
        val paths = mutableListOf<MutableList<LatLng>>()

        coordinates.forEach {
            paths.add(mutableListOf())

            for (i in it.indices step (2)) {
                val latLng = LatLng(it[i], it[i + 1])
                paths.last().add(latLng)
            }
        }

        return paths
    }
}