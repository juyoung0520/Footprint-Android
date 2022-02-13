package com.footprint.footprint.ui.main.mypage

import com.footprint.footprint.data.model.UpdateGoalReqModel

interface GoalNextMonthUpdateView {
    fun onGoalNextMonthUpdateSuccess()
    fun onGoalNextMonthUpdateFail(code: Int?, goal: UpdateGoalReqModel)
}