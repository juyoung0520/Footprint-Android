package com.footprint.footprint.ui.main.mypage

import com.footprint.footprint.data.remote.achieve.AchieveDetailResult
import com.footprint.footprint.data.dto.User

interface MyPageView {
    fun onMyPageLoading()
    fun onMyPageSuccess(achieveDetailResult: AchieveDetailResult)
    fun onUserSuccess(user: User)
    fun onMyPageFailure(code: Int, message: String)
}