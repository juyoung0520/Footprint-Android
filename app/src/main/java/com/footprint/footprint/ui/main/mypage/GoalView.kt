package com.footprint.footprint.ui.main.mypage

import com.footprint.footprint.data.model.GoalModel

interface GoalView {
    fun onGetGoalSuccess(goal: GoalModel)
    fun onGoalFail(code: Int, message: String)
}