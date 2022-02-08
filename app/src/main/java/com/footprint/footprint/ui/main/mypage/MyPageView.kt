package com.footprint.footprint.ui.main.mypage

import com.footprint.footprint.data.remote.achieve.AchieveDetailResult


interface MyPageView {
    fun onMyPageLoading()
    fun onMyPageSuccess(result: AchieveDetailResult)
    fun onMyPageFailure(code: Int, message: String)
}