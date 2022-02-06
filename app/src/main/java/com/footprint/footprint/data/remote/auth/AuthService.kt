package com.footprint.footprint.data.remote.auth

import android.util.Log
import com.footprint.footprint.data.model.SocialUserModel
import com.footprint.footprint.ui.signin.SignInView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AuthService {

    /*로그인 API*/
    fun login(signinView: SignInView, socialUserData: SocialUserModel){
        val authService = retrofit.create(LoginRetrofitInterface::class.java)

        signinView.onSignInLoading()
        authService.login(socialUserData).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val body = response.body()

                Log.d("LOGIN/API-SUCCESS", body.toString())
                when(body!!.code){
                    1000 -> {
                        val result = body.result
                        signinView.onSignInSuccess(result!!)
                    }
                    else -> signinView.onSignInFailure(body.code, body.message)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                signinView.onSignInFailure(213, t.message.toString())
                Log.d("LOGIN/API-FAILURE", t.message.toString())
            }

        })
    }

}