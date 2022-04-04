package com.footprint.footprint.data.remote.badge

import com.footprint.footprint.ui.signin.MonthBadgeView
import com.footprint.footprint.data.dto.BadgeInfo
import com.footprint.footprint.data.dto.BadgeResponse
import com.footprint.footprint.data.dto.ChangeRepresentativeBadgeResponse
import com.footprint.footprint.data.dto.GetBadgeResponse
import com.footprint.footprint.ui.main.mypage.BadgeView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import retrofit2.*

object BadgeService {
    private val badgeService = retrofit.create(BadgeRetrofitInterface::class.java)

    /*이달의 뱃지 조회 API*/
    fun getMonthBadge(monthBadgeView: MonthBadgeView){
        badgeService.getMonthBadge().enqueue(object : Callback<MonthBadgeResponse>{
            override fun onResponse(
                call: Call<MonthBadgeResponse>,
                response: Response<MonthBadgeResponse>
            ) {
                val body = response.body()

                when(body!!.code){
                    1000 -> {
                        //요청 성공
                        val result = body.result
                        monthBadgeView.onMonthBadgeSuccess(true, NetworkUtils.decrypt(result, BadgeInfo::class.java))
                        LogUtils.d("MBADGE/API-SUCCESS", body.toString())
                    }
                    3030 -> {
                        //이번 달에 획득한 뱃지가 없습니다. (PRO, LOVER, MASTER)
                        monthBadgeView.onMonthBadgeSuccess(false, null)
                        LogUtils.d("MBADGE/API-SUCCESS", "이번 달에 획득한 뱃지가 없습니다.")
                    }
                    else -> monthBadgeView.onMonthBadgeFailure(body.code, body.message)
                }
            }

            override fun onFailure(call: Call<MonthBadgeResponse>, t: Throwable) {
                LogUtils.d("MBADGE/API-FAILURE", t.message.toString())
                monthBadgeView.onMonthBadgeFailure(213, t.message.toString())
            }
        })

    }

}