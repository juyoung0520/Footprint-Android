package com.footprint.footprint.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.footprint.footprint.R
import com.footprint.footprint.data.remote.walk.DayResult
import com.footprint.footprint.databinding.ItemCalendarDayBinding
import com.footprint.footprint.ui.main.MainActivity
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.LocalDate

class CalendarDayBinder(val context: Context) : DayBinder<CalendarDayBinder.DayViewContainer> {
    interface OnDayClickListener {
        fun onDayClick(oldSelection: LocalDate?, selection: LocalDate)
    }

    private val currentMonthResults = arrayListOf<DayResult>()
    private val currentMonthDates = arrayListOf<Int>()
    private lateinit var mOnDayClickListener: OnDayClickListener

    // inner class 안에 있으면 안된다.
    private var selectedDate: LocalDate? = null
    private var dayLayoutParams: ConstraintLayout.LayoutParams? = null

    fun setOnDayClickListener(listener: OnDayClickListener) {
        mOnDayClickListener = listener
    }

    fun setCurrentMonthResults(dayResults: List<DayResult>) {
        currentMonthResults.clear()
        currentMonthResults.addAll(dayResults)

        // 날짜만 담기
        currentMonthDates.clear()
        currentMonthResults.map {
            currentMonthDates.add(it.day)
        }
    }

    fun setSelectedDate(localDate: LocalDate) {
        selectedDate = localDate
    }

    fun setDayLayoutParams(width: Int, height: Int) {
        dayLayoutParams = if (width > height) {
            ConstraintLayout.LayoutParams(height, height)
        } else {
            ConstraintLayout.LayoutParams(width, width)
        }

        dayLayoutParams!!.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        dayLayoutParams!!.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        dayLayoutParams!!.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        dayLayoutParams!!.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
    }

    fun getSelectedDate() = selectedDate

    override fun bind(container: DayViewContainer, day: CalendarDay) {
        container.bindDay(day)
    }

    override fun create(view: View): DayViewContainer = DayViewContainer(view)

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        private val binding = ItemCalendarDayBinding.bind(view)

        fun bindDay(day: CalendarDay) {
            binding.calendarDayTv.text = day.date.dayOfMonth.toString()

            if (dayLayoutParams != null) {
                binding.calendarDayStrokeV.layoutParams = dayLayoutParams
                binding.calendarDayBgV.layoutParams = dayLayoutParams
            }

            if (day.owner != DayOwner.THIS_MONTH) {
                setIndicator(0)

                binding.calendarDayTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white_dark
                    )
                )
                return
            } else {
                binding.calendarDayTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black_dark
                    )
                )
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

            if(currentMonthDates.contains(day.date.dayOfMonth)) {
                val idx = currentMonthDates.indexOf(day.date.dayOfMonth)
                when (currentMonthResults[idx].walkCount) {
                    1 -> setIndicator(1)
                    2 -> setIndicator(2)
                    3 -> setIndicator(3)
                    else -> setIndicator(3)
                }
            } else {
                setIndicator(0)
            }

            binding.root.setOnClickListener {
                if (day.owner == DayOwner.THIS_MONTH) {
                    val oldSelection = selectedDate

                    if (oldSelection != day.date) {
                        mOnDayClickListener.onDayClick(oldSelection, day.date)
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
