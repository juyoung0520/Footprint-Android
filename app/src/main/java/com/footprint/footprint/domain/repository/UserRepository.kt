package com.footprint.footprint.domain.repository

import com.footprint.footprint.data.dto.*
import com.footprint.footprint.data.dto.*
import com.footprint.footprint.domain.model.MyInfoUserModel
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.domain.model.InitUserModel


interface UserRepository {
    suspend fun registerUser(userModel: InitUserModel): Result<BaseResponse>
    suspend fun updateUser(myInfoUserModel: MyInfoUserModel): Result<BaseResponse>
    suspend fun getUser(): Result<SimpleUserModel>       //홈, 산책, 마이페이지 유저 정보 SimpleUserModel
    suspend fun getMyInfoUser(): Result<MyInfoUserModel> //내 정보 수정 유저 정보 MyInfoUserModel
}