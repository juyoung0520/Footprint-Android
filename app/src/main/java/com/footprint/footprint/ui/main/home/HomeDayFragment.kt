package com.footprint.footprint.ui.main.home

import android.util.Log
import androidx.navigation.fragment.NavHostFragment
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentHomeDayBinding
import com.footprint.footprint.ui.BaseFragment
import kotlin.math.roundToInt

class HomeDayFragment : BaseFragment<FragmentHomeDayBinding>(FragmentHomeDayBinding::inflate){


    override fun initAfterBinding() {
        //circleProgressBar = binding.homeDayPb
        Log.d("HOMEDAYFRG", "initAfterBinding")

        val currentFragment = NavHostFragment.findNavController(this).currentDestination
        Log.d("day", currentFragment.toString())
    }


    fun receiveData(){
        val circleProgressBar = binding.homeDayPb
        Log.d("HOMEDAYFRG", "onStart")
        val bundle = this.arguments
        Log.d("HOMEDAYFRG", "Bundle null")
        if (bundle != null) {
            Log.d("HOMEDAYFRG", "Bundle 도착")
            val goalRate = bundle.getFloat("TodayGoalRate", 0f).roundToInt()
            val goalTime = bundle.getInt("TodayWalkGoalTime", 0)

            //프로그레스바 & 달성율 텍스트 변경
            circleProgressBar.progress = goalRate
            binding.homeDayProgressTv.text = String.format("%d%%", goalRate)

            //달성율 색 변경: 100(primary), 0(black_dark), else(secondary)
            val color =
                if (goalRate == 100) R.color.primary else if (goalRate == 0) R.color.black_light else R.color.secondary
            circleProgressBar.setProgressStartColor(color)
            circleProgressBar.setProgressEndColor(color)
            binding.homeDayProgressTv.setTextColor(resources.getColor(color))

            //목표 시간 텍스트 변경
            val goalTimeString = if (goalTime < 60)
                String.format(" : %d분", goalTime)
            else {
                if (goalTime % 60 == 0)
                    String.format(" : %d시간", goalTime / 60)
                else
                    String.format(" : %d시간 %d분", goalTime / 60, goalTime % 60)
            }

            binding.homeDayGoalTv.text = goalTimeString
        }
    }

    fun sendDayData(TodayGoalRate: Int, TodayWalkGoalTime: Int){
        Log.d("HOMEDAYFRG", "도착")

        //프로그레스바 & 달성율 텍스트 변경
        val circleProgressBar = binding.homeDayPb
        circleProgressBar.progress = TodayGoalRate
        binding.homeDayProgressTv.text = String.format("%d%%", TodayGoalRate)

        //달성율 색 변경: 100(primary), 0(black_dark), else(secondary)
        val color =
            if (TodayGoalRate == 100) R.color.primary else if (TodayGoalRate == 0) R.color.black_light else R.color.secondary
        circleProgressBar.setProgressStartColor(color)
        circleProgressBar.setProgressEndColor(color)
        binding.homeDayProgressTv.setTextColor(resources.getColor(color))

        //목표 시간 텍스트 변경
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
}