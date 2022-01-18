package com.footprint.footprint.ui.main.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.footprint.footprint.databinding.FragmentHomeMonthBinding
import com.footprint.footprint.ui.BaseFragment
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class HomeMonthFragment: BaseFragment<FragmentHomeMonthBinding>(FragmentHomeMonthBinding::inflate) {
    override fun initAfterBinding() {
        //현재 날짜 받아오기
        var localNowDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
        var nowDate: Date = java.sql.Date.valueOf(localNowDate.toString());

        //리사이클러뷰 어댑터 연결
        var calRVAdapter = HomeMonthRVAdapter(binding.homeMonthCalLayout, nowDate)
        binding.homeMonthCalRv.adapter = calRVAdapter
        binding.homeMonthCalRv.layoutManager = GridLayoutManager(context, 7, LinearLayoutManager.VERTICAL, false)

        //목표 week 설정

    }
}