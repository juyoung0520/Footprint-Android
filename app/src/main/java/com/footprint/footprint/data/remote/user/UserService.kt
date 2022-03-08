package com.footprint.footprint.data.remote.user

import android.util.Log
import com.footprint.footprint.data.model.SimpleUserModel
import com.footprint.footprint.data.model.UserModel
import com.footprint.footprint.ui.main.home.HomeView
import com.footprint.footprint.ui.main.mypage.MyPageView
import com.footprint.footprint.ui.register.RegisterView
import com.footprint.footprint.ui.setting.MyInfoUpdateView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import retrofit2.*

object UserService {
    val userService: UserRetrofitInterface = retrofit.create(UserRetrofitInterface::class.java)

    /*초기 정보 등록 API*/
    fun registerInfos(registerView: RegisterView,  userModel: UserModel) {

        val encryptedData = NetworkUtils.encrypt(userModel)

        userService.registerUser(encryptedData).enqueue(object : Callback<UserRegisterResponse>{
            override fun onResponse(call: Call<UserRegisterResponse>, response: Response<UserRegisterResponse>) {
                val body = response.body()
                if(body != null){
                    when(body.code){
                        1000 -> {
                            registerView.onRegisterSuccess(body.result)
                        }
                        else -> registerView.onRegisterFailure(body.code, body.message)
                    }
                    LogUtils.d("REGISTER/API-SUCCESS", body.toString())
                }else{
                    LogUtils.d("REGISTER/NULL", body.toString())
                }


            }

            override fun onFailure(call: Call<UserRegisterResponse>, t: Throwable) {
                registerView.onRegisterFailure(213, t.message.toString())
                LogUtils.d("REGISTER/API-FAILURE", t.message.toString())
            }

        })
    }

    /*유저 정보 API*/
    fun getUser(view: HomeView){
        userService.getUser().enqueue(object : Callback<UserResponse>{
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val body = response.body()

                LogUtils.d("USER/API-SUCCESS", body.toString())
                when(body!!.code){
                    1000 ->{
                        val result = body.result
                        view.onUserSuccess(NetworkUtils.decrypt(result, User::class.java))
                    }
                    else -> view.onHomeFailure(body.code, body.message)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                view.onHomeFailure(213, t.message.toString())
                LogUtils.d("USER/API-FAILURE", t.message.toString())
            }
        })
    }

    /*유저 정보 API 마이페이지*/
    fun getUser(view: MyPageView){
        userService.getUser().enqueue(object : Callback<UserResponse>{
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val body = response.body()

                LogUtils.d("USER/API-SUCCESS", body.toString())
                when(body!!.code){
                    1000 ->{
                        val result = body.result
                        view.onUserSuccess(NetworkUtils.decrypt(result, User::class.java))
                    }
                    else -> view.onMyPageFailure(body.code, body.message)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                view.onMyPageFailure(213, t.message.toString())
            }
        })
    }


    /*유저 정보 업데이트 API*/
    fun updateUser(view: MyInfoUpdateView, user: SimpleUserModel){

        val encryptedData = NetworkUtils.encrypt(user)

        userService.updateUser(encryptedData).enqueue(object : Callback<UserRegisterResponse>{
            override fun onResponse(call: Call<UserRegisterResponse>, response: Response<UserRegisterResponse>) {
                val body = response.body()

                LogUtils.d("UPDATE/API-SUCCESS", body.toString())
                when(body!!.code){
                    1000 ->{
                        view.onUpdateSuccess(body)
                    }
                    else -> view.onUpdateFailure(body.code, body.message)
                }
            }

            override fun onFailure(call: Call<UserRegisterResponse>, t: Throwable) {
                view.onUpdateFailure(213, t.message.toString())
                LogUtils.d("UPDATE/API-FAILURE", t.message.toString())
            }

        })
    }
}