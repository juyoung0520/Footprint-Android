package com.footprint.footprint.ui.setting

import com.footprint.footprint.data.remote.user.UserRegisterResponse

interface MyInfoUpdateView {
    fun onUpdateSuccess(result: UserRegisterResponse)
    fun onUpdateFailure(code: Int, message: String)
}