package com.footprint.footprint.ui.signin

import com.footprint.footprint.data.remote.auth.Login

interface SignInView {
    fun onSignInLoading()
    fun onSignInSuccess(result: Login)
    fun onSignInFailure(code: Int, message: String)
}