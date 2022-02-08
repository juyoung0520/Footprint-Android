package com.footprint.footprint.data.remote.badge

import android.util.Log
import com.footprint.footprint.ui.main.home.HomeView
import com.footprint.footprint.ui.main.mypage.BadgeView
import com.footprint.footprint.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object BadgeService {
    private val badgeService = GlobalApplication.retrofit.create(BadgeRetrofitInterface::class.java)

    fun getBadgeInfo(badgeView: BadgeView) {
        badgeView.onBadgeLoading()

        badgeService.getBadges().enqueue(object : Callback<GetBadgeResponse> {
            override fun onResponse(
                call: Call<GetBadgeResponse>,
                response: Response<GetBadgeResponse>
            ) {
                val res = response.body()
                Log.d("BadgeService", "\ngetBadgeInfo-RES\ncode: ${res?.code}\nbody: $res")

                when (val code = res?.code) {
                    1000 -> badgeView.onGetBadgeSuccess(res?.result)
                    else -> badgeView.onBadgeFail(code!!, res?.message)
                }
            }

            override fun onFailure(call: Call<GetBadgeResponse>, t: Throwable) {
                Log.e("BadgeService", "getBadgeInfo-ERROR: ${t.message.toString()}")
                badgeView.onBadgeFail(5000, t.message.toString())
            }
        })
    }

    fun changeRepresentativeBadge(badgeView: BadgeView, badgeIdx: Int) {
        badgeService.changeRepresentativeBadge(badgeIdx)
            .enqueue(object : Callback<ChangeRepresentativeBadgeResponse> {
                override fun onResponse(
                    call: Call<ChangeRepresentativeBadgeResponse>,
                    response: Response<ChangeRepresentativeBadgeResponse>
                ) {
                    val res = response.body()
                    Log.d(
                        "BadgeService",
                        "\nchangeRepresentativeBadge-RES\ncode: ${res?.code}\nbody: $res"
                    )

                    when (val code = res?.code) {
                        1000 -> badgeView.onChangeRepresentativeBadge(res?.result)
                        else -> badgeView.onBadgeFail(code!!, res?.message)
                    }
                }

                override fun onFailure(
                    call: Call<ChangeRepresentativeBadgeResponse>,
                    t: Throwable
                ) {
                    Log.e(
                        "BadgeService",
                        "changeRepresentativeBadge-ERROR: ${t.message.toString()}"
                    )
                    badgeView.onBadgeFail(5000, t.message.toString())
                }

            })
    }

    /*이달의 뱃지 조회 API*/
    fun getMonthBadge(homeView: HomeView) {
        badgeService.getMonthBadge().enqueue(object : Callback<MonthBadgeResponse> {
            override fun onResponse(
                call: Call<MonthBadgeResponse>,
                response: Response<MonthBadgeResponse>
            ) {
                val body = response.body()

                when (body!!.code) {
                    1000 -> {
                        //요청 성공
                        val result = body.result
                        homeView.onMonthBadgeSuccess(result!!)
                        Log.d("MBADGE/API-SUCCESS", body.toString())
                    }
                    3030 -> {
                        //이번 달에 획득한 뱃지가 없습니다. (PRO, LOVER, MASTER)
                        Log.d("MBADGE/API-SUCCESS", "이번 달에 획득한 뱃지가 없습니다.")
                    }
                    else -> homeView.onMonthBadgeFailure(body.code, body.message)
                }
            }

            override fun onFailure(call: Call<MonthBadgeResponse>, t: Throwable) {
                Log.d("MBADGE/API-FAILURE", t.message.toString())
                homeView.onMonthBadgeFailure(213, t.message.toString())
            }
        })

    }
}