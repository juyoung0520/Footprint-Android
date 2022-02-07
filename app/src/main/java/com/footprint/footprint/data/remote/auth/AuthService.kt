package com.footprint.footprint.data.remote.auth

import android.util.Log
import com.footprint.footprint.data.model.SocialUserModel
import com.footprint.footprint.ui.signin.SignInView
import com.footprint.footprint.ui.signin.SplashView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AuthService {
    val authService = retrofit.create(LoginRetrofitInterface::class.java)

    /*자동 로그인 API*/
    fun autoLogin(splashView: SplashView){
        authService.autoLogin().enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val body = response.body()

                Log.d("AUTOLOGIN/API-SUCCESS", body.toString())
                if(body!= null){
                    when(body.code){
                        1000 -> {
                            val result = body.result
                            splashView.onAutoLoginSuccess(result)
                        }
                        else -> splashView.onAutoLoginFailure(body.code, body.message)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("AUTOLOGIN/API-FAILURE", t.message.toString())
                splashView.onAutoLoginFailure(213, t.message.toString())
            }

        })
    }


    /*로그인 API*/
    fun login(signinView: SignInView, socialUserData: SocialUserModel){

        authService.login(socialUserData).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val body = response.body()

                Log.d("LOGIN/API-SUCCESS", body.toString())
                if(body!= null){
                    when(body.code){
                        1000 -> {
                            val result = body.result
                            signinView.onSignInSuccess(result)
                        }
                        else -> signinView.onSignInFailure(body.code, body.message)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                signinView.onSignInFailure(213, t.message.toString())
                Log.d("LOGIN/API-FAILURE", t.message.toString())
            }

        })
    }

}