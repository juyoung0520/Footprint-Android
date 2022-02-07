package com.footprint.footprint.ui.setting

import com.footprint.footprint.data.remote.user.UserResponse

interface MyInfoUpdateView {
    fun onUpdateSuccess(result: UserResponse)
    fun onUpdateFailure(code: Int, message: String)
}