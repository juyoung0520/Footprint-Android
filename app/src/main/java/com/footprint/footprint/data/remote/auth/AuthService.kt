package com.footprint.footprint.data.remote.auth

import com.footprint.footprint.data.model.SocialUserModel
import com.footprint.footprint.ui.setting.SettingView
import com.footprint.footprint.ui.signin.SignInView
import com.footprint.footprint.ui.signin.SplashView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
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

                LogUtils.d("AUTOLOGIN/API-SUCCESS", body.toString())
                if(body!= null){
                    when(body.code){
                        1000 -> {
                            val result = body.result
                            splashView.onAutoLoginSuccess(NetworkUtils.decrypt(result, Login::class.java))
                        }
                        else -> splashView.onAutoLoginFailure(body.code, body.message)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                LogUtils.d("AUTOLOGIN/API-FAILURE", t.message.toString())
                splashView.onAutoLoginFailure(213, t.message.toString())
            }

        })
    }


    /*로그인 API*/
    fun login(signinView: SignInView, socialUserData: SocialUserModel){

        val encryptedData = NetworkUtils.encrypt(socialUserData)
        LogUtils.d("kakao-user", "암호화 후 데이터: ${encryptedData}")

        //MediaType.parse("application/json; charset=utf-8")

        //val data = encryptedData.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        //val data = encryptedData.toRequestBody("application/json".toMediaTypeOrNull())

        //val data = encryptedData.toRequestBody("application/json; charset=utf-8".toMediaType())
        val data = encryptedData.toRequestBody("application/json".toMediaType())

        LogUtils.d("data", data.toString())

        authService.login(data).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val body = response.body()

                LogUtils.d("LOGIN/API-SUCCESS", body.toString())
                if(body!= null){
                    when(body.code){
                        1000 -> {
                            val result = body.result
                            signinView.onSignInSuccess(NetworkUtils.decrypt(result, Login::class.java))
                        }
                        else -> signinView.onSignInFailure(body.code, body.message)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                signinView.onSignInFailure(213, t.message.toString())
                LogUtils.d("LOGIN/API-FAILURE", t.message.toString())
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

                LogUtils.d("UNREGISTER/API-SUCCESS", body.toString())
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
                LogUtils.d("UNREGISTER/API-FAILURE", t.message.toString())
                settingView.onUnregisterFailure(213, t.message.toString())
            }

        })
    }

}