package com.footprint.footprint.data.remote.user

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.data.dto.User
import com.footprint.footprint.ui.main.mypage.MyPageView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import retrofit2.*

object UserService {
    val userService: UserRetrofitInterface = retrofit.create(UserRetrofitInterface::class.java)

    /*유저 정보 API 마이페이지*/
    fun getUser(view: MyPageView){
        userService.getUser().enqueue(object : Callback<BaseResponse>{
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
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

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                view.onMyPageFailure(213, t.message.toString())
            }
        })
    }
}