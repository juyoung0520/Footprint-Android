package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.LoginDTO
import com.footprint.footprint.domain.model.SocialUserModel

interface AuthRepository {
    suspend fun autoLogin(): Result<LoginDTO>
    suspend fun login(socialUserData: SocialUserModel): Result<LoginDTO>
    suspend fun unregister(): Result<BaseResponse>
}