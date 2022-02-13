package com.footprint.footprint.ui.main.home

import com.footprint.footprint.data.remote.user.User


interface HomeView {
    /*유저 정보 받아오기*/
    fun onUserSuccess(user: User)
    fun onHomeFailure(code: Int, message: String)
}