package com.footprint.footprint.domain.usecase

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.data.dto.LoginDTO
import com.footprint.footprint.domain.model.SocialUserModel
import com.footprint.footprint.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository)  {
    suspend operator fun invoke(socialUserModel: SocialUserModel): Result<LoginDTO> = repository.login(socialUserModel)
}