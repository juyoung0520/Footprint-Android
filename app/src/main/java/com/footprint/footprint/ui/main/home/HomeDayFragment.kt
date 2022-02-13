package com.footprint.footprint.ui.main.home

import android.graphics.Color
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.footprint.footprint.data.remote.achieve.Today
import com.footprint.footprint.databinding.FragmentHomeDayBinding
import com.footprint.footprint.ui.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeDayFragment() : BaseFragment<FragmentHomeDayBinding>(FragmentHomeDayBinding::inflate),
    HomeDayView {

    private lateinit var today: Today
    private lateinit var job: Job

    override fun initAfterBinding() {
        setLoadingBar(true) //초기 상태
    }

    override fun onResume() {
        super.onResume()
        //프래그먼트 다시 돌아왔을 때 ex. 산책 액티비티에서 메인으로 다시 돌아옴
        if(::today.isInitialized){
            setDayFragment()
            setLoadingBar(false)
        }else{
            setLoadingBar(true)
        }
    }

    /*API-SUCCESS*/
    override fun onTodaySuccess(today: Today) {
        this.today = today
        if(view != null){
            job = viewLifecycleOwner.lifecycleScope.launch{
                setDayFragment()
                setLoadingBar(false)
            }
        }
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

    override fun onDestroyView() {
        if(::job.isInitialized)
            job.cancel()
        super.onDestroyView()
    }
}