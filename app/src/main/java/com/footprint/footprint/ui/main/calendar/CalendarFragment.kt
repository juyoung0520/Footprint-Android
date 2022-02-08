package com.footprint.footprint.ui.main.calendar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.databinding.FragmentCalendarBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CalendarDayBinder
import com.footprint.footprint.ui.adapter.WalkRVAdapter
import com.footprint.footprint.ui.lock.LockActivity
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceWidth
import com.footprint.footprint.utils.getPWDstatus
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

class CalendarFragment() : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {
    private lateinit var currentMonth: YearMonth
    private lateinit var calendarDayBinder: CalendarDayBinder
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private var job: Job? = null

    override fun initAfterBinding() {
        setBinding()

        initCalendar()

        initWalkAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //잠금 해제 성공
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //1. 잠금 해제 로그 & 토스트
                Log.d("CALENDAR/LOCK", "잠금 해제 성공")
                Toast.makeText(context, "잠금 해제 성공", Toast.LENGTH_SHORT).show()

                //2. 산책 기록 프래그먼트로 이동
            }
        }
    }

    private fun setBinding() {
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

        binding.calendarSearchIv.setOnClickListener {
            val action = CalendarFragmentDirections.actionCalendarFragmentToSearchFragment()
            findNavController().navigate(action)
        }
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
                if (view != null) {
                    binding.calendarMonthTitleTv.text = "${p1.year}.${p1.month}"
                    currentMonth = p1.yearMonth
                }
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

    private fun initWalkAdapter() {
        val walks = arrayListOf<WalkModel>()
        walks.apply {
            add(WalkModel(0))
            add(WalkModel(1))
            add(WalkModel(2))
            add(WalkModel(3))
            add(WalkModel(4))
        }

        binding.calendarWalkNumber2Tv.text = " ${walks.size}"

        val adapter = WalkRVAdapter()
        adapter.setWalks(walks)

        adapter.setOnItemClickListener(object : WalkRVAdapter.OnItemClickListener {
            override fun onItemClick(walk: WalkModel) {
                lockUnlock(walk.walkIdx)
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
    }

    fun changeDayOfWeek(dayOfWeek: String): String {
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

    /*암호 확인*/
    private fun lockUnlock(walkIdx: Int) {
        val pwdStatus = getPWDstatus(requireContext())
        if (pwdStatus == "ON") {
            //잠금 해제 액티비티(LockActivity)로 이동, UNLOCK: 잠금 해제 모드
            val intent = Intent(requireContext(), LockActivity::class.java)
            intent.putExtra("mode", "UNLOCK")
            resultLauncher.launch(intent)
        } else {
            // 잠금 없음
            //1. 잠금 없음 로그 & 토스트
            Log.d("CALENDAR/LOCK", "잠금 없음")
            Toast.makeText(context, "${walkIdx}번째 산책", Toast.LENGTH_SHORT).show()

            //2. 산책 기록 프래그먼트로 이동
        }
    }

    override fun onDestroyView() {
        if (job != null) job!!.cancel()
        super.onDestroyView()
    }
}