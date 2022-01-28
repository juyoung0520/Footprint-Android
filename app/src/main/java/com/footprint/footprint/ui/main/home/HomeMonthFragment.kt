package com.footprint.footprint.ui.main.home

import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.footprint.footprint.databinding.FragmentHomeMonthBinding
import com.footprint.footprint.ui.BaseFragment
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.convertPxToDp
import com.footprint.footprint.utils.getDeviceHeight
import com.footprint.footprint.utils.setHeight


class HomeMonthFragment :
    BaseFragment<FragmentHomeMonthBinding>(FragmentHomeMonthBinding::inflate) {

    override fun initAfterBinding() {
        //현재 날짜 받아오기
        var localNowDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
        var nowDate: Date = java.sql.Date.valueOf(localNowDate.toString());
        initMonthFragment(nowDate)
    }

    /*MonthFragment*/
    private fun initMonthFragment(nowDate: Date) {
        //Calendar 클래스 연결
        var homemonthCalendar = HomeMonthCalendar(nowDate)
        homemonthCalendar.initBaseCalendar()

        val dataList = homemonthCalendar.dateList //date 리스트
        val weeks = homemonthCalendar.weeks //주수
        val firstDateIndex = homemonthCalendar.prevTail //첫번째 날
        val lastDateIndex = dataList.size - homemonthCalendar.nextHead - 1 //마지막 날
        val today = nowDate.date //오늘

        val pxMargin = calMargin(weeks)


        //리사이클러뷰 어댑터 연결
        var calRVAdapter =
            HomeMonthRVAdapter(dataList, today, firstDateIndex, lastDateIndex, pxMargin)
        binding.homeMonthCalRv.adapter = calRVAdapter
        binding.homeMonthCalRv.layoutManager =
            GridLayoutManager(context, 7, LinearLayoutManager.VERTICAL, false)
    }

    private fun calMargin(weeks: Int): Int {
        //기본 설정
        val dpHeight = convertPxToDp(requireContext(), getDeviceHeight())
        Log.d("DISPLAY-SIZE", "dpHeight = ${dpHeight}")

        //RV item margin 구하기
        val VPArea = dpHeight * 0.5 - 47
        val MarginArea = VPArea - (32 * weeks + 14)
        val margin = MarginArea / (weeks - 1)
        val pxMargin = convertDpToPx(requireContext(), margin.toInt())

        //Background height와 padding 구하기
        val height = VPArea - (14 + margin * 2)
        val pxHeight = convertDpToPx(requireContext(), height.toInt())
        binding.homeMonthBackgroundLayout.setHeight(pxHeight)
        binding.homeMonthBackgroundLayout.setPadding(0, pxMargin / 2, 0, pxMargin / 2)
        return pxMargin
    }
}
