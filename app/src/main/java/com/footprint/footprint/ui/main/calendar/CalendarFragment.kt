package com.footprint.footprint.ui.main.calendar

import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentCalendarBinding
import com.footprint.footprint.ui.BaseFragment

class CalendarFragment() : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {

    override fun initAfterBinding() {
        //WalkDetailActivity 화면으로 이동
        binding.calendarContentTv.setOnClickListener {
            findNavController()?.navigate(R.id.walkDetailActivity)
        }
    }
}