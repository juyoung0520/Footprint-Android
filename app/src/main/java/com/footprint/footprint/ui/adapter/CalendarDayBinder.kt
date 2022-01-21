package com.footprint.footprint.ui.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ItemCalendarDayBinding
import com.footprint.footprint.ui.main.MainActivity
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.LocalDate

class CalendarDayBinder(val context: Context) : DayBinder<CalendarDayBinder.DayViewContainer> {
    interface OnDayClickListener {
        fun onDayClick(selection: LocalDate)
    }

    private lateinit var mOnDayClickListener: OnDayClickListener

    // inner class 안에 있으면 안된다.
    private var selectedDate: LocalDate? = null

    fun setOnDayClickListener(listener: OnDayClickListener) {
        mOnDayClickListener = listener
    }

    fun setSelectedDate(date: LocalDate) {
        selectedDate = date
    }

    override fun bind(container: DayViewContainer, day: CalendarDay) {
        container.bindDay(day)
    }

    override fun create(view: View): DayViewContainer = DayViewContainer(view)

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        private val binding = ItemCalendarDayBinding.bind(view)

        fun bindDay(day: CalendarDay) {
            binding.calendarDayTv.text = day.date.dayOfMonth.toString()

            if (day.owner != DayOwner.THIS_MONTH) {
                binding.calendarDayTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white_dark
                    )
                )
                return
            }

            if (day.owner == DayOwner.PREVIOUS_MONTH) {
                Log.d("out", day.date.toString())
            }

            if (day.date == selectedDate) {
                binding.calendarDayStrokeV.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_primary_stroke_round)
            } else {
                binding.calendarDayStrokeV.background = null
            }

            if (day.date == LocalDate.now()) {
                binding.calendarDayBgV.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_white_2_round)
                binding.calendarDayTv.setTextColor(ContextCompat.getColor(context, R.color.primary))
            } else {
                binding.calendarDayBgV.background = null
            }

            setIndicator(day.date.dayOfMonth % 3)

            binding.root.setOnClickListener {
                if (day.owner == DayOwner.THIS_MONTH) {
                    val tmpSelectedDate = selectedDate

                    if (tmpSelectedDate != day.date) {
                        selectedDate = day.date
                        mOnDayClickListener.onDayClick(selectedDate!!)

                        if (tmpSelectedDate != null) {
                            mOnDayClickListener.onDayClick(tmpSelectedDate)
                        }
                    }
                }
            }
        }

        private fun setIndicator(walkNum: Int) {
            when (walkNum) {
                1 -> {
                    binding.calendarIndicatorWalk1V.visibility = View.GONE
                    binding.calendarIndicatorWalk2V.visibility = View.GONE
                    binding.calendarIndicatorWalk3V.visibility = View.VISIBLE
                }
                2 -> {
                    binding.calendarIndicatorWalk1V.visibility = View.VISIBLE
                    binding.calendarIndicatorWalk2V.visibility = View.GONE
                    binding.calendarIndicatorWalk3V.visibility = View.VISIBLE
                }
                3 -> {
                    binding.calendarIndicatorWalk1V.visibility = View.VISIBLE
                    binding.calendarIndicatorWalk2V.visibility = View.VISIBLE
                    binding.calendarIndicatorWalk3V.visibility = View.VISIBLE
                }
                else -> {
                    binding.calendarIndicatorWalk1V.visibility = View.GONE
                    binding.calendarIndicatorWalk2V.visibility = View.GONE
                    binding.calendarIndicatorWalk3V.visibility = View.GONE
                }
            }

        }
    }
}
