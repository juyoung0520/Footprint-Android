package com.footprint.footprint.data.remote.goal

import com.footprint.footprint.data.model.UpdateGoalReqModel
import com.footprint.footprint.data.remote.walk.BaseResponse
import com.footprint.footprint.ui.main.mypage.GoalNextMonthUpdateView
import com.footprint.footprint.ui.main.mypage.GoalView
import com.footprint.footprint.utils.GlobalApplication
import com.footprint.footprint.utils.isNetworkAvailable
import gun0912.tedimagepicker.util.ToastUtil.context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GoalService {
    private val goalService = GlobalApplication.retrofit.create(GoalRetrofitInterface::class.java)

    fun getThisMonthGoal(goalView: GoalView) {
        goalService.getThisMonthGoal().enqueue(object : Callback<GetGoalResponse> {
            override fun onResponse(call: Call<GetGoalResponse>, response: Response<GetGoalResponse>) {
                val res = response.body()

                if (res?.code==1000)
                    goalView.onGetGoalSuccess(res?.result!!)
                else
                    goalView.onGoalFail(res?.code)
            }

            override fun onFailure(call: Call<GetGoalResponse>, t: Throwable) {
                if (!isNetworkAvailable(context))
                    goalView.onGoalFail(5000)
                else
                    goalView.onGoalFail(6000)
            }
        })
    }

    fun getNextMonthGoal(goalView: GoalView) {
        goalService.getNextMonthGoal().enqueue(object : Callback<GetGoalResponse> {
            override fun onResponse(call: Call<GetGoalResponse>, response: Response<GetGoalResponse>) {
                val res = response.body()

                if (res?.code==1000)
                    goalView.onGetGoalSuccess(res?.result!!)
                else
                    goalView.onGoalFail(res?.code)
            }

            override fun onFailure(call: Call<GetGoalResponse>, t: Throwable) {
                if (!isNetworkAvailable(context))
                    goalView.onGoalFail(5000)
                else
                    goalView.onGoalFail(6000)
            }
        })
    }

    fun updateGoal(goalView: GoalNextMonthUpdateView, goal: UpdateGoalReqModel) {
        goalService.updateGoal(goal).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                val res = response.body()

                if (res?.code==1000)
                    goalView.onGoalNextMonthUpdateSuccess()
                else
                    goalView.onGoalNextMonthUpdateFail(res?.code, goal)
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                if (!isNetworkAvailable(context))
                    goalView.onGoalNextMonthUpdateFail(6000, goal)
                else
                    goalView.onGoalNextMonthUpdateFail(5000, goal)
            }

        })
    }
}