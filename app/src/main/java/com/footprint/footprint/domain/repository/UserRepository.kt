package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.SimpleUser

interface UserRepository {
    suspend fun updateUser(user: SimpleUser): Result<SimpleUser>
}