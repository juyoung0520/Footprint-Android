package com.footprint.footprint.data.remote.achieve

import retrofit2.*
import com.footprint.footprint.ui.main.mypage.MyPageView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.NetworkUtils


object AchieveService {

    /*마이페이지 정보(통계) API*/
    fun getInfoDetail(myPageView: MyPageView) {
        val infoService = retrofit.create(AchieveRetrofitInterface::class.java)

        myPageView.onMyPageLoading()

        infoService.getInfoDetail().enqueue(object : Callback<AchieveDetailResponse>{
            override fun onResponse(
                call: Call<AchieveDetailResponse>,
                response: Response<AchieveDetailResponse>
            ) {
                val resp = response.body()!!

                when(resp.code) {
                    1000 -> myPageView.onMyPageSuccess(NetworkUtils.decrypt(resp.result, AchieveDetailResult::class.java))
                    else -> myPageView.onMyPageFailure(resp.code, resp.message)
                }
            }

            override fun onFailure(call: Call<AchieveDetailResponse>, t: Throwable) {
                myPageView.onMyPageFailure(400, t.message.toString())
            }

        })
    }

}