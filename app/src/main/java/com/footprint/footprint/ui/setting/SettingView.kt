package com.footprint.footprint.ui.setting

import com.footprint.footprint.data.remote.auth.UnRegisterResponse

interface SettingView {
    fun onUnregisterSuccess(result: UnRegisterResponse)
    fun onUnregisterFailure(code: Int, message: String)
}