package com.footprint.footprint.ui.register

interface RegisterView {
    fun onRegisterLoading()
    fun onRegisterSuccess(result: String?)
    fun onRegisterFailure(code: Int, message: String)
}