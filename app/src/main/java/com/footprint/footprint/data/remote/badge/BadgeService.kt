package com.footprint.footprint.data.remote.badge

import android.util.Log
import com.footprint.footprint.ui.signin.BadgeView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import retrofit2.*

object BadgeService {
    private val badgeService = retrofit.create(BadgeRetrofitInterface::class.java)

    /*이달의 뱃지 조회 API*/
    fun getMonthBadge(badgeView: BadgeView){
        badgeService.getMonthBadge().enqueue(object : Callback<MonthBadgeResponse>{
            override fun onResponse(
                call: Call<MonthBadgeResponse>,
                response: Response<MonthBadgeResponse>
            ) {
                val body = response.body()

                when(body!!.code){
                    1000 ->{
                        //요청 성공
                        val result = body.result
                        badgeView.onMonthBadgeSuccess(true, result)
                        Log.d("MBADGE/API-SUCCESS", body.toString())
                    }
                    3030 -> {
                        //이번 달에 획득한 뱃지가 없습니다. (PRO, LOVER, MASTER)
                        badgeView.onMonthBadgeSuccess(false, null)
                        Log.d("MBADGE/API-SUCCESS", "이번 달에 획득한 뱃지가 없습니다.")
                    }
                    else -> badgeView.onMonthBadgeFailure(body.code, body.message)
                }
            }

            override fun onFailure(call: Call<MonthBadgeResponse>, t: Throwable) {
                Log.d("MBADGE/API-FAILURE", t.message.toString())
                badgeView.onMonthBadgeFailure(213, t.message.toString())
            }
        })

    }

}