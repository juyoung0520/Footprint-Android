package com.footprint.footprint.ui.setting

import com.footprint.footprint.data.remote.user.UserRegisterResponse
import com.footprint.footprint.data.remote.user.UserResponse

interface MyInfoUpdateView {
    fun onUpdateSuccess(result: UserRegisterResponse)
    fun onUpdateFailure(code: Int, message: String)
}