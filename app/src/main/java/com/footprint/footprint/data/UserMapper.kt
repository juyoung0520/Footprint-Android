package com.footprint.footprint.data

import com.footprint.footprint.data.dto.User
import com.footprint.footprint.domain.model.UserInfoModel

object UserMapper {
    fun mapperToUserInfo(user: User): UserInfoModel{
        return UserInfoModel(
            user.sex,
            user.height,
            user.weight,
            0,
            user.walkNumber
        )
    }
}