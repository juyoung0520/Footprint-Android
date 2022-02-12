package com.footprint.footprint.ui.main.home

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.footprint.footprint.data.remote.achieve.TMonth
import com.footprint.footprint.data.remote.achieve.TMonthDayRateRes
import com.footprint.footprint.databinding.FragmentHomeMonthBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.HomeMonthRVAdapter
import com.footprint.footprint.utils.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class HomeMonthFragment() :
    BaseFragment<FragmentHomeMonthBinding>(FragmentHomeMonthBinding::inflate), HomeMonthView {

    private lateinit var tMonth: TMonth
    private lateinit var calRVAdapter: HomeMonthRVAdapter

    override fun initAfterBinding() {

    }

    override fun onResume() {
        super.onResume()
        if(::tMonth.isInitialized){
            setMonthFragment()
            setLoadingBar(false)
        }else{
            setLoadingBar(true)
        }

    }

    /*일별 정보 조회 API*/
    override fun onTMonthSuccess(tMonth: TMonth) {
        this.tMonth = tMonth
    }
    /*프래그먼트 설정*/
    private fun setMonthFragment(){
        //1. 목표 요일 설정
        val goalDays = tMonth.goalDayList
        for(day in goalDays){
            when(day){
                "MON" -> binding.homeMonthMonBackgroundIv.visibility = View.VISIBLE
                "TUE" -> binding.homeMonthTueBackgroundIv.visibility = View.VISIBLE
                "WED" -> binding.homeMonthWedBackgroundIv.visibility = View.VISIBLE
                "THU" -> binding.homeMonthThuBackgroundIv.visibility = View.VISIBLE
                "FRI" -> binding.homeMonthFriBackgroundIv.visibility = View.VISIBLE
                "SAT" -> binding.homeMonthSatBackgroundIv.visibility = View.VISIBLE
                "SUN" -> binding.homeMonthSunBackgroundIv.visibility = View.VISIBLE
            }
        }

        //2. RVA 연결
        //현재 날짜 받아오기
        val localNowDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val nowDate: Date = java.sql.Date.valueOf(localNowDate.toString())

        //디바이스 넓, 높이 구하기
        val widthPx = getDeviceWidth() - convertDpToPx(requireContext(), 60) //넓이 - 양 옆 마진(30*2)
        val heightPx = getDeviceHeight()
        val vpAreaPx = heightPx * 0.5 - convertDpToPx(requireContext(), 47 + 14 + 60 + 5) //VP 넓이 - (TB 높이 + 요일 title 높이 + topMargin)
        val itemMaxPx = convertDpToPx(requireContext(), 32)

        //리사이클러뷰 어댑터 연결
        calRVAdapter =
            HomeMonthRVAdapter(nowDate, tMonth.getDayRateRes, widthPx, vpAreaPx.toInt(), itemMaxPx)
        binding.homeMonthCalRv.adapter = calRVAdapter
        binding.homeMonthCalRv.layoutManager =
            GridLayoutManager(context, 7, LinearLayoutManager.VERTICAL, false)
    }

    /*로딩 바*/
    private fun setLoadingBar(btn: Boolean){
        if(btn){
            //ON
            binding.homeMonthLoadingPb.visibility = View.VISIBLE
            binding.homeMonthLoadingBgV.visibility = View.VISIBLE
        }else{
            //OFF
            binding.homeMonthLoadingPb.visibility = View.GONE
            binding.homeMonthLoadingBgV.visibility = View.GONE
        }
    }
}
