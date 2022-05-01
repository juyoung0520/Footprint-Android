package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.Login
import com.footprint.footprint.domain.model.SocialUserModel
import com.footprint.footprint.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository)  {
    suspend operator fun invoke(socialUserModel: SocialUserModel): Result<Login> = repository.login(socialUserModel)
}