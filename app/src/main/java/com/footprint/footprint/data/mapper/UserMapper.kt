package com.footprint.footprint.data.mapper

import com.footprint.footprint.data.dto.UserDTO
import com.footprint.footprint.domain.model.MyInfoUserModel
import com.footprint.footprint.domain.model.SimpleUserModel

object UserMapper {
    fun mapperToSimpleUser(user: UserDTO): SimpleUserModel{
        return SimpleUserModel(
            user.nickname,
            user.sex,
            user.height,
            user.weight,
            0,
            user.walkNumber,
            user.badgeUrl
        )
    }

    fun mapperToMyInfoUser(user: UserDTO): MyInfoUserModel{
        return MyInfoUserModel(
            user.nickname,
            user.sex,
            user.birth,
            user.height,
            user.weight
        )
    }
}