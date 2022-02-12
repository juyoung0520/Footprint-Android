package com.footprint.footprint.ui.main.home

import android.graphics.Color
import android.util.Log
import android.view.View
import com.footprint.footprint.data.remote.achieve.Today
import com.footprint.footprint.databinding.FragmentHomeDayBinding
import com.footprint.footprint.ui.BaseFragment

class HomeDayFragment() : BaseFragment<FragmentHomeDayBinding>(FragmentHomeDayBinding::inflate),
    HomeDayView {

    private lateinit var today: Today

    override fun initAfterBinding() {
        setLoadingBar(true)
    }

    override fun onTodaySuccess(today: Today) {
        this.today = today
        setDayFragment()
        setLoadingBar(false)
    }

    private fun setDayFragment() {
        val TodayGoalRate = today.goalRate.toInt()
        val TodayWalkGoalTime = today.walkGoalTime

        //1. 프로그레스바 & 달성율
        val circleProgressBar = binding.homeDayPb
        circleProgressBar.progress = TodayGoalRate
        binding.homeDayProgressTv.text = String.format("%d%%", TodayGoalRate)

        //달성율 색 변경: 100(primary), 0(black_dark), else(secondary)
        val color =
            if (TodayGoalRate == 100) "#4FB8E7" else if (TodayGoalRate == 0) "#241F20" else "#FFC01D"
        circleProgressBar.setProgressStartColor(Color.parseColor(color))
        circleProgressBar.setProgressEndColor(Color.parseColor(color))
        binding.homeDayProgressTv.setTextColor(Color.parseColor(color))

        //2. 목표 시간 텍스트 변경
        val goalTimeString = if (TodayWalkGoalTime < 60)
            String.format(" : %d분", TodayWalkGoalTime)
        else {
            if (TodayWalkGoalTime % 60 == 0)
                String.format(" : %d시간", TodayWalkGoalTime / 60)
            else
                String.format(" : %d시간 %d분", TodayWalkGoalTime / 60, TodayWalkGoalTime % 60)
        }

        binding.homeDayGoalTv.text = goalTimeString
    }

    /*로딩 바*/
    private fun setLoadingBar(btn: Boolean){
        if(btn){
            //ON
            binding.homeDayLoadingPb.visibility = View.VISIBLE
            binding.homeDayLoadingBgV.visibility = View.VISIBLE
        }else{
            //OFF
            binding.homeDayLoadingPb.visibility = View.GONE
            binding.homeDayLoadingBgV.visibility = View.GONE
        }
    }
}