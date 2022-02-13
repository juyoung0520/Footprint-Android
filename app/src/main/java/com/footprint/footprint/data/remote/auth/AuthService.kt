package com.footprint.footprint.data.remote.auth

import com.footprint.footprint.data.model.SocialUserModel
import com.footprint.footprint.ui.setting.SettingView
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
                splashView.onAutoLoginFailure(213, t.message.toString())
            }

        })
    }


    /*로그인 API*/
    fun login(signinView: SignInView, socialUserData: SocialUserModel){

        authService.login(socialUserData).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val body = response.body()

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
            }

        })
    }

    fun unregister(settingView: SettingView){
        authService.unregister().enqueue(object : Callback<UnRegisterResponse>{
            override fun onResponse(
                call: Call<UnRegisterResponse>,
                response: Response<UnRegisterResponse>
            ) {
                val body = response.body()

                if(body!= null){
                    when(body.code){
                        1000 -> {
                            settingView.onUnregisterSuccess(body)
                        }
                        else -> {
                            settingView.onUnregisterFailure(body.code, body.message)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UnRegisterResponse>, t: Throwable) {
                settingView.onUnregisterFailure(213, t.message.toString())
            }

        })
    }

}