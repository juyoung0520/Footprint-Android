package com.footprint.footprint.ui.main.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.databinding.FragmentCalendarBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CalendarDayBinder
import com.footprint.footprint.ui.adapter.WalkRVAdapter
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeiviceWidth
import com.kizitonwose.calendarview.Completion
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthScrollListener
import com.kizitonwose.calendarview.utils.Size
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.roundToInt

class CalendarFragment() : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {
    private lateinit var currentMonth: YearMonth

    override fun initAfterBinding() {
        initCalendar()

        initWalkAdapter()
    }

    private fun initCalendar() {
        val width = getDeiviceWidth() * 0.9
        val cellWidth = (width / 7).roundToInt()

        binding.calendarWalkCv.daySize = Size(cellWidth, convertDpToPx(requireContext(), 40))

        val caledarDayBinder = CalendarDayBinder(requireContext())
        caledarDayBinder.setOnDayClickListener(object : CalendarDayBinder.OnDayClickListener {
            override fun onDayClick(selection: LocalDate) {
                binding.calendarWalkCv.notifyDateChanged(selection)
            }
        })

        binding.calendarWalkCv.dayBinder = caledarDayBinder

        currentMonth = YearMonth.now()

        binding.calendarMonthTitleTv.text = "${currentMonth.year}.${currentMonth.monthValue}"

        val firstMonth = currentMonth.minusMonths(120)
        val lastMonth = currentMonth.plusMonths(120)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        binding.calendarWalkCv.setupAsync(
            firstMonth,
            lastMonth,
            firstDayOfWeek,
            object : Completion {
                override fun invoke() {
                    binding.calendarLoadingBgV.visibility = View.GONE
                    binding.calendarLoadingPb.visibility = View.GONE

                    binding.calendarWalkCv.scrollToMonth(currentMonth)

                    afterInitCalendar()
                }
            })
    }

    private fun afterInitCalendar() {
        binding.calendarWalkCv.monthScrollListener = object : MonthScrollListener {
            override fun invoke(p1: CalendarMonth) {
                binding.calendarMonthTitleTv.text = "${p1.year}.${p1.month}"
                currentMonth = p1.yearMonth
            }
        }

        binding.calendarLeftIv.setOnClickListener {
            currentMonth = currentMonth.previous
            binding.calendarWalkCv.scrollToMonth(currentMonth)
        }

        binding.calendarRightIv.setOnClickListener {
            currentMonth = currentMonth.next
            binding.calendarWalkCv.scrollToMonth(currentMonth)
        }

        binding.calendarTodayTv.setOnClickListener {
            currentMonth = YearMonth.now()
            binding.calendarWalkCv.scrollToDate(LocalDate.now())
        }
    }

    private fun initWalkAdapter() {
        val walks = arrayListOf<WalkModel>()
        walks.apply {
            add(WalkModel(0))
            add(WalkModel(1))
            add(WalkModel(2))
            add(WalkModel(3))
            add(WalkModel(4))
        }

        val adapter = WalkRVAdapter()
        adapter.setWalks(walks)

        // 좀 더 생각해보자
        adapter.walks.observe(viewLifecycleOwner) { walks ->
            Log.d("walks", walks.size.toString())
            if (walks.isEmpty()) {
                binding.calendarHintTv.visibility = View.VISIBLE
                binding.calendarWalkRv.visibility = View.GONE
            } else {
                binding.calendarHintTv.visibility = View.GONE
                binding.calendarWalkRv.visibility = View.VISIBLE
            }
        }

        adapter.setOnItemClickListener(object : WalkRVAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(context,"${position}번 째 산책", Toast.LENGTH_SHORT)
            }
        })
        binding.calendarWalkRv.adapter = adapter
        binding.calendarWalkRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
}