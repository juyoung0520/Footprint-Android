package com.footprint.footprint.data.remote.badge

import android.util.Log
import com.footprint.footprint.ui.main.mypage.BadgeView
import com.footprint.footprint.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object BadgeService {
    private val badgeService = GlobalApplication.retrofit.create(BadgeRetrofitInterface::class.java)

    fun getBadgeInfo(badgeView: BadgeView) {
        badgeView.onBadgeLoading()

        badgeService.getBadges().enqueue(object: Callback<GetBadgeResponse> {
            override fun onResponse(
                call: Call<GetBadgeResponse>,
                response: Response<GetBadgeResponse>
            ) {
                val res = response.body()
                Log.d("BadgeService","\ngetBadgeInfo-RES\ncode: ${res?.code}\nbody: $res")

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
        badgeService.changeRepresentativeBadge(badgeIdx).enqueue(object : Callback<ChangeRepresentativeBadgeResponse> {
            override fun onResponse(
                call: Call<ChangeRepresentativeBadgeResponse>,
                response: Response<ChangeRepresentativeBadgeResponse>
            ) {
                val res = response.body()
                Log.d("BadgeService","\nchangeRepresentativeBadge-RES\ncode: ${res?.code}\nbody: $res")

                when (val code = res?.code) {
                    1000 -> badgeView.onChangeRepresentativeBadge(res?.result)
                    else -> badgeView.onBadgeFail(code!!, res?.message)
                }
            }

            override fun onFailure(call: Call<ChangeRepresentativeBadgeResponse>, t: Throwable) {
                Log.e("BadgeService", "changeRepresentativeBadge-ERROR: ${t.message.toString()}")
                badgeView.onBadgeFail(5000, t.message.toString())
            }

        })
    }

}