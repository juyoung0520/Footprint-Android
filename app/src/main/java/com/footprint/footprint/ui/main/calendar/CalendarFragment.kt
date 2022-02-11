package com.footprint.footprint.ui.main.calendar

import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.footprint.footprint.data.remote.walk.DayResult
import com.footprint.footprint.data.remote.walk.DayWalkResult
import com.footprint.footprint.data.remote.walk.UserDateWalk
import com.footprint.footprint.data.remote.walk.WalkService
import com.footprint.footprint.databinding.FragmentCalendarBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CalendarDayBinder
import com.footprint.footprint.ui.adapter.WalkRVAdapter
import com.footprint.footprint.utils.GlobalApplication.Companion.TAG
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceWidth
import com.kizitonwose.calendarview.Completion
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthScrollListener
import com.kizitonwose.calendarview.utils.Size
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.roundToInt

class CalendarFragment() : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate),
    CalendarView {
    private lateinit var currentMonth: YearMonth
    private lateinit var calendarDayBinder: CalendarDayBinder

    private val jobs = arrayListOf<Job>()

    override fun initAfterBinding() {
        // 캘린더 초기화 됐으면
        if (!::calendarDayBinder.isInitialized) {
            setBinding()
            initCalendar()
            return
        }

        // currentMonth 초기화 됐으면 이번달 API 호출
        if (::currentMonth.isInitialized) {
            WalkService.getMonthWalks(this, currentMonth.year, currentMonth.monthValue)
        }

        // 선택된 날이 없으면 오늘 날짜 API 호출
        val date = calendarDayBinder.getSelectedDate() ?: LocalDate.now()
        WalkService.getDayWalks(
            this,
            String.format("%d-%02d-%02d", date.year, date.monthValue, date.dayOfMonth)
        )
    }

    private fun setBinding() {
        binding.calendarSearchIv.setOnClickListener {
            val action = CalendarFragmentDirections.actionCalendarFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        val localDate = LocalDate.now()
        binding.calendarMonthTitleTv.text =
            String.format("%d.%d", localDate.year, localDate.monthValue)
        binding.calendarSelectedDayTv.text =
            String.format(
                "%d.%d.%d %s",
                localDate.year,
                localDate.monthValue,
                localDate.dayOfMonth,
                changeDayOfWeek(localDate.dayOfWeek.toString())
            )
        //DayWalk API 호출
        WalkService.getDayWalks(
            this,
            String.format("%d-%02d-%02d", localDate.year, localDate.monthValue, localDate.dayOfMonth)
        )
    }

    private fun initCalendar() {
        val width = getDeviceWidth() * 0.9
        val cellWidth = (width / 7).roundToInt()
        val cellHeight = convertDpToPx(requireContext(), 50)

        binding.calendarWalkCv.daySize = Size(cellWidth, cellHeight)

        calendarDayBinder = CalendarDayBinder(requireContext())
        calendarDayBinder.setDayLayoutParams(cellWidth, cellHeight)

        calendarDayBinder.setOnDayClickListener(object : CalendarDayBinder.OnDayClickListener {
            override fun onDayClick(oldSelection: LocalDate?, selection: LocalDate) {
                selectDate(oldSelection, selection)
            }
        })

        binding.calendarWalkCv.dayBinder = calendarDayBinder


        currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(60)
        val lastMonth = currentMonth.plusMonths(60)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek


        binding.calendarWalkCv.setupAsync(
            firstMonth,
            lastMonth,
            firstDayOfWeek,
            object : Completion {
                override fun invoke() {
                    if (view != null) {
                        jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                            binding.calendarWalkCv.scrollToMonth(currentMonth)
                            afterInitCalendar()
                        })
                    }
                }
            })
    }


    private fun afterInitCalendar() {
        binding.calendarWalkCv.monthScrollListener = object : MonthScrollListener {
            override fun invoke(p1: CalendarMonth) {
                binding.calendarLoadingBgV.visibility = View.VISIBLE
                binding.calendarLoadingPb.visibility = View.VISIBLE

                currentMonth = p1.yearMonth
                // Month API 호출
                WalkService.getMonthWalks(this@CalendarFragment, p1.year, p1.month)
                binding.calendarMonthTitleTv.text = "${p1.year}.${p1.month}"
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
            val localDate = LocalDate.now()
            currentMonth = localDate.yearMonth

            val oldSelection = calendarDayBinder.getSelectedDate()

            binding.calendarWalkCv.scrollToDate(localDate)
            selectDate(oldSelection, localDate)
        }

    }

    private fun initWalkAdapter(walks: List<DayWalkResult>) {
        binding.calendarWalkNumber2Tv.text = " ${walks.size}"

        val adapter = WalkRVAdapter(requireContext())
        adapter.setFragmentManager(requireActivity().supportFragmentManager)
        adapter.setWalks(walks)

        adapter.setOnItemClickListener(object : WalkRVAdapter.OnItemClickListener {
            override fun onItemClick(walk: UserDateWalk) {
                goWalkDetailActivity(walk.walkIdx)
            }
        })

        adapter.setOnItemRemoveClickListener(object : WalkRVAdapter.OnItemRemoveClickListener {
            override fun onItemRemoveClick() {
                val itemCount = adapter.itemCount
                if (itemCount == 0) {
                    binding.calendarHintTv.visibility = View.VISIBLE
                    binding.calendarWalkRv.visibility = View.GONE
                } else {
                    binding.calendarHintTv.visibility = View.GONE
                    binding.calendarWalkRv.visibility = View.VISIBLE
                }

                binding.calendarWalkNumber2Tv.text = " $itemCount"
            }
        })

        binding.calendarWalkRv.adapter = adapter
        binding.calendarWalkRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    fun selectDate(oldSelection: LocalDate?, selection: LocalDate) {
        calendarDayBinder.setSelectedDate(selection)
        binding.calendarWalkCv.notifyDateChanged(selection)

        if (oldSelection != null) {
            binding.calendarWalkCv.notifyDateChanged(oldSelection)
        }

        binding.calendarSelectedDayTv.text =
            String.format(
                "%d.%d.%d %s",
                selection.year,
                selection.monthValue,
                selection.dayOfMonth,
                changeDayOfWeek(selection.dayOfWeek.toString())
            )
        //DayWalk API 호출
        WalkService.getDayWalks(
            this,
            String.format("%d-%02d-%02d", selection.year, selection.monthValue, selection.dayOfMonth)
        )
    }

    private fun changeDayOfWeek(dayOfWeek: String): String {
        return when (dayOfWeek) {
            "MONDAY" -> "월"
            "TUESDAY" -> "화"
            "WEDNESDAY" -> "수"
            "THURSDAY" -> "목"
            "FRIDAY" -> "금"
            "SATURDAY" -> "토"
            "SUNDAY" -> "일"
            else -> ""
        }
    }

    //WalkDetailActivity 로 이동하는 함수
    fun goWalkDetailActivity(walkIdx: Int) {
        val action = CalendarFragmentDirections.actionCalendarFragmentToWalkDetailActivity2(walkIdx)   //날짜별 산책 데이터 조회 API 가 연결됐을 때 사용
        findNavController().navigate(action)
    }

    override fun onMonthLoading() {
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.calendarLoadingBgV.visibility = View.VISIBLE
                binding.calendarLoadingPb.visibility = View.VISIBLE
            })
        }
    }

    override fun onDayWalkLoading() {
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.calendarHintTv.visibility = View.VISIBLE
                binding.calendarWalkRv.visibility = View.GONE
            })
        }
    }

    override fun onCalendarFailure(code: Int, message: String) {
        when (code) {
            // Month
            400 -> {
                Log.d("$TAG/CALENDAR/API", "CALENDAR/MONTH/$message")
                if (view != null) {
                    jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                        binding.calendarLoadingBgV.visibility = View.GONE
                        binding.calendarLoadingPb.visibility = View.GONE
                    })
                }
            }
            401 -> {
                Log.d("$TAG/CALENDAR/API", "CALENDAR/DAY-WALK/$message")
            }
            else -> {
                Log.d("$TAG/CALENDAR/", "CALENDAR/DAY-WALK/$message")
            }
        }
    }

    override fun onMonthSuccess(monthResult: List<DayResult>) {
        Log.d("$TAG/SEARCH-RESULT", "CALENDAR/MONTH/success")

        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                calendarDayBinder.setCurrentMonthResults(monthResult)
                binding.calendarWalkCv.notifyMonthChanged(currentMonth)

                binding.calendarLoadingBgV.visibility = View.GONE
                binding.calendarLoadingPb.visibility = View.GONE
            })
        }
    }

    override fun onDayWalksSuccess(dayWalkResult: List<DayWalkResult>) {
        Log.d("$TAG/SEARCH-RESULT", "CALENDAR/DAY-WALK/success")

        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                initWalkAdapter(dayWalkResult)

                if (dayWalkResult.isNotEmpty()) {
                    binding.calendarHintTv.visibility = View.GONE
                    binding.calendarWalkRv.visibility = View.VISIBLE
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        jobs.map {
            it.cancel()
        }
    }
}