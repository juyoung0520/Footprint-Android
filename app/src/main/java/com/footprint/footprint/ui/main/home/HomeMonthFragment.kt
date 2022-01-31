package com.footprint.footprint.ui.main.home

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.footprint.footprint.databinding.FragmentHomeMonthBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class HomeMonthFragment :
    BaseFragment<FragmentHomeMonthBinding>(FragmentHomeMonthBinding::inflate) {

    override fun initAfterBinding() {
        //현재 날짜 받아오기
        var localNowDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
        var nowDate: Date = java.sql.Date.valueOf(localNowDate.toString())

        //디바이스 넓, 높이 구하기
        val widthPx = getDeviceWidth() - convertDpToPx(requireContext(), 60) //넓이 - 양 옆 마진(30*2)
        val heightPx = getDeviceHeight()
        val vpAreaPx = heightPx * 0.5 - convertDpToPx(requireContext(), 47 + 14 + 60 + 5) //VP 넓이 - (TB 높이 + 요일 title 높이 + topMargin)

        //리사이클러뷰 어댑터 연결
        var calRVAdapter =
            HomeMonthRVAdapter(nowDate, widthPx, vpAreaPx.toInt())
        binding.homeMonthCalRv.adapter = calRVAdapter
        binding.homeMonthCalRv.layoutManager =
            GridLayoutManager(context, 7, LinearLayoutManager.VERTICAL, false)
    }

}
