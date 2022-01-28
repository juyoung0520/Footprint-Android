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
import android.view.View



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

        //기본 설정
        val dpHeight = initHeight()
        val density = context?.resources?.displayMetrics?.density

        //RV item margin 구하기
        val VPArea = dpHeight * 0.5 - 47
        val MarginArea = VPArea - (32 * weeks + 14)
        val margin = MarginArea / (weeks - 1)
        val pxMargin = dp2px(density!!, margin.toInt())

        //Background height와 padding 구하기
        val height = VPArea - (14 + margin * 2)
        val pxHeight = dp2px(density, height.toInt())
        binding.homeMonthBackgroundLayout.setHeight(pxHeight)
        binding.homeMonthBackgroundLayout.setPadding(0, pxMargin/2, 0, pxMargin/2)

        //리사이클러뷰 어댑터 연결
        var calRVAdapter =
            HomeMonthRVAdapter(dataList, today, firstDateIndex, lastDateIndex, pxMargin)
        binding.homeMonthCalRv.adapter = calRVAdapter
        binding.homeMonthCalRv.layoutManager =
            GridLayoutManager(context, 7, LinearLayoutManager.VERTICAL, false)
    }

    /*Utils*/
    //Display 높이 구하는 함수
    private fun initHeight(): Float {
        val outMetrics = DisplayMetrics()
        var display: Display?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            display = activity?.windowManager?.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getMetrics(outMetrics)
        }

        val density = resources.displayMetrics.density
        val dpHeight = outMetrics.heightPixels / density
        val dpWidth = outMetrics.widthPixels / density

        Log.d("DISPLAY-SIZE", "dpHeight = ${dpHeight} dpWidth = ${dpWidth}")
        return dpHeight
    }

    //뷰의 params.height 지정해 주는 함수
    private fun View.setHeight(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.height = value
            layoutParams = lp
        }
    }

    //dp to px 변환 함수 (params)
    private fun dp2px(density:Float, dp: Int): Int {
        return Math.round(dp.toFloat() * density)
    }
}