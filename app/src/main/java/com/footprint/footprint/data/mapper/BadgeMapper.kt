package com.footprint.footprint.data.mapper

import com.footprint.footprint.data.dto.SaveWalkBadgeResDTO
import com.footprint.footprint.domain.model.BadgeEntity

object BadgeMapper {
    fun mapperToBadgeEntityList(badges: List<SaveWalkBadgeResDTO>): List<BadgeEntity> {
        val badgeEntityList: ArrayList<BadgeEntity> = arrayListOf()

        for (badge in badges) {
            badgeEntityList.add(BadgeEntity(badgeIdx = badge.badgeIdx, badgeName = badge.badgeName, badgeUrl = badge.badgeUrl))
        }

        return badgeEntityList
    }
}