package com.footprint.footprint.data.remote.badge

import com.footprint.footprint.ui.main.mypage.BadgeView
import com.footprint.footprint.ui.signin.MonthBadgeView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.isNetworkAvailable
import gun0912.tedimagepicker.util.ToastUtil.context
import retrofit2.*

object BadgeService {
    private val badgeService = retrofit.create(BadgeRetrofitInterface::class.java)

    /*뱃지 목록 조회 API*/
    fun getBadgeInfo(badgeView: BadgeView) {
        badgeView.onBadgeLoading()

        badgeService.getBadges().enqueue(object : Callback<GetBadgeResponse> {
            override fun onResponse(
                call: Call<GetBadgeResponse>,
                response: Response<GetBadgeResponse>
            ) {
                val res = response.body()
                LogUtils.d("BadgeService", "\ngetBadgeInfo-RES\ncode: ${res?.code}\nbody: $res")

                when (val code = res?.code) {
                    1000 -> badgeView.onGetBadgeSuccess(res?.result)
                    else -> badgeView.onGetBadgeFail(code)
                }
            }

            override fun onFailure(call: Call<GetBadgeResponse>, t: Throwable) {
                LogUtils.e("BadgeService", "getBadgeInfo-ERROR: ${t.message.toString()}")

                if (!isNetworkAvailable(context))
                    badgeView.onGetBadgeFail(6000)
                else
                    badgeView.onGetBadgeFail(5000)
            }
        })
    }

    /*대표 뱃지 변경 API*/
    fun changeRepresentativeBadge(badgeView: BadgeView, badgeIdx: Int) {
        badgeService.changeRepresentativeBadge(badgeIdx)
            .enqueue(object : Callback<ChangeRepresentativeBadgeResponse> {
                override fun onResponse(
                    call: Call<ChangeRepresentativeBadgeResponse>,
                    response: Response<ChangeRepresentativeBadgeResponse>
                ) {
                    val res = response.body()
                    LogUtils.d("BadgeService", "\nchangeRepresentativeBadge-RES\ncode: ${res?.code}\nbody: $res")

                    when (val code = res?.code) {
                        1000 -> badgeView.onChangeRepresentativeBadgeSuccess(res?.result)
                        else -> badgeView.onChangeRepresentativeBadgeFail(code, badgeIdx)
                    }
                }

                override fun onFailure(
                    call: Call<ChangeRepresentativeBadgeResponse>,
                    t: Throwable
                ) {
                    LogUtils.e("BadgeService", "changeRepresentativeBadge-ERROR: ${t.message.toString()}")

                    if (!isNetworkAvailable(context))
                        badgeView.onChangeRepresentativeBadgeFail(6000, badgeIdx)
                    else
                        badgeView.onChangeRepresentativeBadgeFail(5000, badgeIdx)
                }

            })
    }

    /*이달의 뱃지 조회 API*/
    fun getMonthBadge(monthBadgeView: MonthBadgeView){
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
                        monthBadgeView.onMonthBadgeSuccess(true, result)
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