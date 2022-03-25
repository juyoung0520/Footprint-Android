package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.*
import com.footprint.footprint.data.model.*
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.domain.model.UserInfoModel
import com.footprint.footprint.domain.model.UserModel


interface UserRepository {
    suspend fun registerUser(userModel: UserModel): Result<BaseResponse>
    suspend fun updateUser(simpleUserModel: SimpleUserModel): Result<BaseResponse>
    suspend fun getUser(): Result<User>
}