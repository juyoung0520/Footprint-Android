package com.footprint.footprint.ui.main.home

import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

class HomeFragment(): BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {


    override fun initAfterBinding() {
        binding.homeContentTv.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.startNextActivity(WalkActivity::class.java)
        }

        val tbTitle = arrayListOf("일별", "월별")
        val homeVPAdapter = HomeViewpagerAdapter(this)
        binding.homeDaymonthVp.adapter = homeVPAdapter

        TabLayoutMediator(binding.homeDaymonthTb, binding.homeDaymonthVp){ tab, position ->
            tab.text = tbTitle[position]
        }.attach()

        //현재 날짜 받아오기
        var nowDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
        var dayOfWeek = when(nowDate.dayOfWeek.value){
            1 -> '월'
            2 -> '화'
            3 -> '수'
            4 -> '목'
            5 -> '금'
            6 -> '토'
            7 -> '일'
            else -> ' '
        }
        binding.homeTopDateTv.text = String.format("%d. %d. %d %c", nowDate.year, nowDate.month.value, nowDate.dayOfMonth, dayOfWeek)
    }

    /*private fun setMyClickListener() {
        binding.homeContentTv.setOnClickListener {
            startActivity(Intent(requireContext(), WalkAfterActivity::class.java))
        }
    }*/


}