package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.Login
import com.footprint.footprint.domain.model.SocialUserModel

interface AuthRepository {
    suspend fun autoLogin(): Result<Login>
    suspend fun login(socialUserData: SocialUserModel): Result<Login>
    suspend fun unregister(): Result<BaseResponse>
}