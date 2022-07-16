package com.footprint.footprint.ui.main.calendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.DayWalkDTO
import com.footprint.footprint.data.dto.UserDateWalkDTO
import com.footprint.footprint.databinding.FragmentCalendarBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CalendarDayBinder
import com.footprint.footprint.ui.adapter.WalkRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.error.ErrorActivity
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceWidth
import com.footprint.footprint.viewmodel.CalendarViewModel
import com.google.android.material.snackbar.Snackbar
import com.kizitonwose.calendarview.Completion
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthScrollListener
import com.kizitonwose.calendarview.utils.Size
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.roundToInt

class CalendarFragment() : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {
    private lateinit var currentMonth: YearMonth
    private lateinit var calendarDayBinder: CalendarDayBinder
    private var isFromFragment = false
    private var currentDeleteWalkIdx: Int? = null

    private val calendarVM: CalendarViewModel by viewModel()
    private lateinit var networkErrSb: Snackbar

    private val jobs = arrayListOf<Job>()

    override fun initAfterBinding() {
        // Activity에서 다시 온 경우
        if (!isFromFragment && ::currentMonth.isInitialized) {
            // API만 다시 호출
            updateAll()
            return
        }

        // Fragment에서 다시 온 경우
        if (isFromFragment) isFromFragment = false

        setBinding()
        initCalendar()
        observe()
    }

    private fun observe() {
        calendarVM.mutableErrorType.observe(viewLifecycleOwner, Observer {
            binding.calendarLoadingPb.visibility = View.VISIBLE

            /* 여기 */
            when (it) {
                ErrorType.NETWORK -> showSnackBar(getString(R.string.error_network))
                else -> startErrorActivity("CalendarFragment")
            }
        })

        calendarVM.monthDays.observe(viewLifecycleOwner, Observer {
            calendarDayBinder.setCurrentMonthResults(it)
            binding.calendarWalkCv.notifyMonthChanged(currentMonth)

            binding.calendarLoadingBgV.visibility = View.GONE
            binding.calendarLoadingPb.visibility = View.GONE
        })

        calendarVM.dayWalks.observe(viewLifecycleOwner, Observer {
            LogUtils.d("Calendar/dayWalks", it.toString())
            initWalkAdapter(it)

            if (it.isNotEmpty()) {
                binding.calendarHintTv.visibility = View.GONE
                binding.calendarWalkRv.visibility = View.VISIBLE
            }
        })

        calendarVM.isDelete.observe(viewLifecycleOwner, Observer {
            updateAll()
        })

    }

    private fun getMonthWalks(year: Int, month: Int) {
        calendarVM.getMonthWalks(year, month)
        binding.calendarLoadingBgV.visibility = View.VISIBLE
        binding.calendarLoadingPb.visibility = View.VISIBLE
    }

    private fun getDayWalks(date: LocalDate) {
        calendarVM.getDayWalks(
            String.format(
                "%d-%02d-%02d",
                date.year,
                date.monthValue,
                date.dayOfMonth
            )
        )
        binding.calendarHintTv.visibility = View.VISIBLE
        binding.calendarWalkRv.visibility = View.GONE
    }

    private fun updateAll() {
        // 선택된 날이 없으면 오늘 날짜 API 호출
        val date = calendarDayBinder.getSelectedDate() ?: LocalDate.now()
        getDayWalks(date)

        // currentMonth 초기화 됐으면 이번달 API 호출
        if (::currentMonth.isInitialized) {
            getMonthWalks(currentMonth.year, currentMonth.monthValue)
        }
    }

    private fun setBinding() {
        binding.calendarSearchIv.setOnClickListener {
            val action = CalendarFragmentDirections.actionCalendarFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        val date =
            // fragement에서 돌아왔을 경우
            if (::calendarDayBinder.isInitialized && calendarDayBinder.getSelectedDate() != null)
                calendarDayBinder.getSelectedDate()!!
            else
                LocalDate.now()

        binding.calendarMonthTitleTv.text =
            String.format("%d.%d", date.year, date.monthValue)
        binding.calendarSelectedDayTv.text =
            String.format(
                "%d.%d.%d %s",
                date.year,
                date.monthValue,
                date.dayOfMonth,
                changeDayOfWeek(date.dayOfWeek.toString())
            )

        //DayWalk API 호출
        getDayWalks(date)
    }

    private fun initCalendar() {
        val width = getDeviceWidth() * 0.9
        val cellWidth = (width / 7).roundToInt()
        val cellHeight = convertDpToPx(requireContext(), 50)

        binding.calendarWalkCv.daySize = Size(cellWidth, cellHeight)

        if (!::calendarDayBinder.isInitialized) {
            calendarDayBinder = CalendarDayBinder(requireContext())
            calendarDayBinder.setDayLayoutParams(cellWidth, cellHeight)

            calendarDayBinder.setOnDayClickListener(object : CalendarDayBinder.OnDayClickListener {
                override fun onDayClick(oldSelection: LocalDate?, selection: LocalDate) {
                    selectDate(oldSelection, selection)
                }
            })
        }

        binding.calendarWalkCv.dayBinder = calendarDayBinder


        if (!::currentMonth.isInitialized) {
            currentMonth = YearMonth.now()
        }
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
                binding.calendarMonthTitleTv.text = "${p1.year}.${p1.month}"

                currentMonth = p1.yearMonth
                // Month API 호출
                getMonthWalks(p1.year, p1.month)
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

    private fun initWalkAdapter(walks: List<DayWalkDTO>) {
        binding.calendarWalkNumber2Tv.text = " ${walks.size}"

        val adapter = WalkRVAdapter(requireContext())
        adapter.setWalks(walks)

        adapter.setOnItemClickListener(object : WalkRVAdapter.OnItemClickListener {
            override fun onItemClick(walk: UserDateWalkDTO) {
                goWalkDetailActivity(walk.walkIdx)
            }
        })

        adapter.setOnItemRemoveClickListener(object : WalkRVAdapter.OnItemRemoveClickListener {
            override fun onItemRemoveClick(walkIdx: Int) {
                showRemoveDialog(walkIdx)
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
        getDayWalks(selection)
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

    private fun showRemoveDialog(walkIdx: Int) {
        val actionDialogFragment = ActionDialogFragment()

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun action1(isAction: Boolean) {
                if (isAction) {
                    // deleteWalk API
                    calendarVM.deleteWalk(walkIdx)
                    currentDeleteWalkIdx = walkIdx
                }
            }

            override fun action2(isAction: Boolean) {
            }
        })

        val bundle = Bundle()
        bundle.putString("msg", "'${walkIdx}번째 산책' 을 삭제하시겠어요?")
        bundle.putString("action", getString(R.string.action_delete))

        actionDialogFragment.arguments = bundle
        actionDialogFragment.show(childFragmentManager, null)
    }

    //WalkDetailActivity 로 이동하는 함수
    fun goWalkDetailActivity(walkIdx: Int) {
        val action =
            CalendarFragmentDirections.actionCalendarFragmentToWalkDetailActivity2(walkIdx)   //날짜별 산책 데이터 조회 API 가 연결됐을 때 사용
        findNavController().navigate(action)
    }

    private fun showSnackBar(errorMessage: String) {
        networkErrSb = Snackbar.make(
            requireView(),
            errorMessage,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(getString(R.string.action_retry)) {
            if (calendarVM.getErrorType() == "deleteWalk") {
                // 삭제 API이면
                calendarVM.deleteWalk(currentDeleteWalkIdx!!)
            } else {
                updateAll()
            }
        }

        networkErrSb.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // SearchFragment 갈 때
        isFromFragment = true

        jobs.map {
            it.cancel()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }
}