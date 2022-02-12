package com.footprint.footprint.ui.signin

import com.footprint.footprint.data.remote.auth.Login
import com.footprint.footprint.data.remote.badge.BadgeInfo

interface SignInView {
    fun onSignInSuccess(result: Login)
    fun onSignInFailure(code: Int, message: String)
}