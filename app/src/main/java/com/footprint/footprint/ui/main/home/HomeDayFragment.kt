package com.footprint.footprint.ui.main.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.footprint.footprint.data.dto.TodayDTO
import com.footprint.footprint.databinding.FragmentHomeDayBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.viewmodel.HomeViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeDayFragment() : BaseFragment<FragmentHomeDayBinding>(FragmentHomeDayBinding::inflate){

    private lateinit var today: TodayDTO
    private val homeVm: HomeViewModel by sharedViewModel()

    override fun initAfterBinding() {
        setLoadingBar(true) //초기 상태
        observe()
    }

    /*Life Cycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // onPause에서 임시 저장된 today 정보 받아오기
        if(savedInstanceState != null) {
            val jsonToday = savedInstanceState.getString("TODAY")
            today = Gson().fromJson(jsonToday, TodayDTO::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        //프래그먼트 다시 돌아왔을 때 ex. 산책 액티비티에서 메인으로 다시 돌아옴
        if(::today.isInitialized){
            bind()
            setLoadingBar(false)
        }else{
            setLoadingBar(true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // onPause 시 날라가는 데이터 임시 저장
        val jsonToday = Gson().toJson(today)
        outState.putString("TODAY", jsonToday)
    }

    /*Observe & Bind*/
    private fun observe(){
        homeVm.thisToday.observe(viewLifecycleOwner, Observer{
            this@HomeDayFragment.today = it
            bind()
            setLoadingBar(false)
        })
    }

    private fun bind() {
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