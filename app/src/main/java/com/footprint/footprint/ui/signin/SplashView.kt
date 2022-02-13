package com.footprint.footprint.ui.signin

import com.footprint.footprint.data.remote.auth.Login

interface SplashView {
    fun onAutoLoginSuccess(result: Login?)
    fun onAutoLoginFailure(code: Int, message: String)
}