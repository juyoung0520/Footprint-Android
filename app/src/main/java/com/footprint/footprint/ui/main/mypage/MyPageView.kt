package com.footprint.footprint.ui.main.mypage

import com.footprint.footprint.data.remote.user.InfoDetailResult

interface MyPageView {
    fun onMyPageLoading()
    fun onMyPageSuccess(result: InfoDetailResult)
    fun onMyPageFailure(code: Int, message: String)
}