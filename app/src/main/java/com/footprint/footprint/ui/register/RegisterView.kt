package com.footprint.footprint.ui.register

interface RegisterView {
    fun onRegisterSuccess(result: String?)
    fun onRegisterFailure(code: Int, message: String)
}