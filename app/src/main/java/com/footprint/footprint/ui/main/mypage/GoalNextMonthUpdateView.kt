package com.footprint.footprint.ui.main.mypage

interface GoalNextMonthUpdateView {
    fun onGoalNextMonthUpdateSuccess()
    fun onGoalNextMonthUpdateFail(code: Int, message: String)
}