package com.footprint.footprint.ui.main.mypage

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentMypageBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.classes.custom.CustomBarChartRender
import com.footprint.footprint.data.remote.achieve.*
import com.footprint.footprint.data.remote.user.User
import com.footprint.footprint.data.remote.user.UserService
import com.footprint.footprint.utils.loadSvg
import com.footprint.footprint.utils.isNetworkAvailable
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.ArrayList

class MyPageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate),
    MyPageView {
    private var isInitialized = false
    private var isFromFragment = false
    private val jobs = arrayListOf<Job>()

    override fun initAfterBinding() {
        if (isFromFragment && !isInitialized) {
            setBinding()
            isInitialized = true
        }

        // 사용자, 통계 API 호출
        UserService.getUser(this)
        AchieveService.getInfoDetail(this)
    }

    private fun setBinding() {
        binding.mypageInfoRightIv.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_badgeFragment)
        }

        binding.mypageGoalRightIv.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_goalThisMonthFragment)
        }

        binding.mypageSettingIv.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_navigation)
        }

        initChart(binding.mypageStatisticsWeekChartBc)
        binding.mypageStatisticsWeekChartBc.apply {
            xAxis.apply {
                textColor = requireContext().getColor(R.color.black)
                valueFormatter =
                    XAxisFormatter(arrayListOf<String>("일", "월", "화", "수", "목", "금", "토"))
            }
        }

        initChart(binding.mypageStatisticsMonthCountChartLc)

        initChart(binding.mypageStatisticsMonthRateChartBc)
        binding.mypageStatisticsMonthRateChartBc.apply {
            xAxis.apply {
                textColor = requireContext().getColor(R.color.black_light)
                valueFormatter = XAxisFormatter(getRecentMonths(true))
            }
        }
    }

    private fun setAchieveDetailResult(result: AchieveDetailResult) {
        // 사용자 달성률
        val userInfoAchieve = result.userInfoAchieve
        binding.mypageTodayPb.progress = userInfoAchieve.todayGoalRate
        binding.mypageTodayProgressTv.text = "${userInfoAchieve.todayGoalRate}%"
        binding.mypageMonthPb.progress = userInfoAchieve.monthGoalRate
        binding.mypageMonthProgressTv.text = "${userInfoAchieve.monthGoalRate}%"
        binding.mypageCountNumberTv.text = "${userInfoAchieve.userWalkCount}회"

        // 글자 색상
        val spanColorPrimary =
            ForegroundColorSpan(requireContext().getColor(R.color.primary))
        val spanColorSecondary =
            ForegroundColorSpan(requireContext().getColor(R.color.secondary))

        // 이번달 목표
        val userGoalRes = result.getUserGoalRes
        binding.mypageGoalRightIv.setOnClickListener {
            val action =
                MyPageFragmentDirections.actionMypageFragmentToGoalThisMonthFragment(userGoalRes)
            findNavController().navigate(action)
        }
        binding.mypageGoalWeekTv.text =
            getSpannableString(
                "주 회",
                userGoalRes.dayIdx.size.toString(),
                2,
                spanColorPrimary
            )
        binding.mypageGoalDayTv.text =
            getSpannableString(
                "하루 분",
                userGoalRes.userGoalTime.walkGoalTime!!.toString(),
                3,
                spanColorPrimary
            )
        binding.mypageGoalTimeTv.text =
            convertToWalkTimeSlotToStr(userGoalRes.userGoalTime.walkTimeSlot!!)

        // 통계
        val userInfoStat = result.userInfoStat
        if (userInfoStat.mostWalkDay[0] == getString(R.string.mst_no_walk_during_3_months)) {
            binding.mypageStatisticsWeekResultTv.text = userInfoStat.mostWalkDay[0]
        } else {
            binding.mypageStatisticsWeekResultTv.text = getSpannableString(
                getString(R.string.msg_statistics_week_res),
                getMostWalkDay(userInfoStat.mostWalkDay),
                3,
                spanColorSecondary
            )
        }
        binding.mypageStatisticsMonthCountResultTv.text = getSpannableString(
            getString(R.string.msg_statistics_month_count_res),
            userInfoStat.thisMonthWalkCount.toString(),
            5,
            spanColorSecondary
        )
        binding.mypageStatisticsMonthRateResultTv.text = getSpannableString(
            getString(R.string.msg_statistics_month_rate_res),
            userInfoStat.thisMonthGoalRate.toString(),
            5,
            spanColorSecondary
        )

        // week 그래프 데이터 설정
        setWeekChartData(userInfoStat.userWeekDayRate)
        //월별 달성률 그래프 데이터 설정
        setMonthRateChartData(userInfoStat.monthlyGoalRate)
        // 월별 기록 횟수 데이터 설정
        setMonthCountChartData(userInfoStat.monthlyWalkCount)
    }

    // 글자색 바꾸는 함수
    private fun getSpannableString(
        originText: CharSequence,
        insertText: String,
        startIdx: Int,
        color: ForegroundColorSpan
    ): SpannableStringBuilder {
        val spannableText = SpannableStringBuilder(originText)
        spannableText.apply {
            insert(startIdx, insertText)
            setSpan(
                color,
                startIdx,
                startIdx + insertText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // SDK 28이상인 기기에서는 글씨체 변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            spannableText.setSpan(
                TypefaceSpan(resources.getFont(R.font.namusquareround_extra_bold)),
                startIdx,
                startIdx + insertText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableText
    }

    private fun initChart(chart: BarChart) {
        val colorGray = requireContext().getColor(R.color.white_caption)

        // 커스텀 막대 radius 설정
        val barChartRender =
            CustomBarChartRender(
                chart,
                chart.animator,
                chart.viewPortHandler
            )
        barChartRender.setRadius(30)

        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)
            setTouchEnabled(false)
            setPadding(19, 10, 16, 10)
            extraBottomOffset = 15f
            renderer = barChartRender
            axisLeft.apply {
                axisMaximum = 101f
                axisMinimum = 0f
                granularity = 20f
                textSize = 10f
                typeface = resources.getFont(R.font.namusquareround_extra_bold)
                textColor = colorGray
                gridColor = colorGray
                gridLineWidth = 1f
                setDrawLabels(true) // y축 값
                setDrawGridLines(true)
                setDrawAxisLine(false)
            }
            xAxis.apply {
                yOffset = 10f
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                typeface = resources.getFont(R.font.namusquareround_bold)
                granularity = 1f
                setDrawLabels(true) // x축 값
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
        }
    }

    private fun initChart(chart: LineChart) {
        val colorGray = requireContext().getColor(R.color.white_caption)

        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)
            setTouchEnabled(false)
            setPadding(25, 10, 25, 10)
            extraBottomOffset = 15f
            axisLeft.apply {
                axisMaximum = 50f
                axisMinimum = 0f
                granularity = 10f
                setLabelCount(6, false)
                textSize = 10f
                typeface = resources.getFont(R.font.namusquareround_extra_bold)
                textColor = colorGray
                gridColor = colorGray
                gridLineWidth = 1f
                setDrawLabels(true) // y축 값
                setDrawGridLines(true)
                setDrawAxisLine(false)
            }
            xAxis.apply {
                spaceMin = 0.5f
                spaceMax = 0.5f
                yOffset = 10f
                position = XAxis.XAxisPosition.BOTTOM
                textColor = requireContext().getColor(R.color.black_light)
                valueFormatter = XAxisFormatter(getRecentMonths(false))
                textSize = 12f
                typeface = resources.getFont(R.font.namusquareround_bold)
                granularity = 1f
                setDrawLabels(true) // x축 값
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
        }
    }

    private fun setWeekChartData(weekRate: List<Double>) {
        // 값
        val maxValue = weekRate.maxOrNull()

        // 그래프에 엔트리 넣고, 색깔 리스트 설정
        val entries = arrayListOf<BarEntry>()
        val colors = arrayListOf<Int>()

        for (idx in weekRate.indices) {
            entries.add(BarEntry((idx + 1).toFloat(), weekRate[idx].toFloat()))
            if (maxValue == weekRate[idx]) {
                colors.add(requireContext().getColor(R.color.secondary))
            } else {
                colors.add(requireContext().getColor(R.color.primary))
            }
        }

        // 막대 생성 해서 엔트리 적용
        val dataSet = BarDataSet(entries, "DataSet")
        dataSet.colors = colors
        dataSet.setDrawValues(false)

        val iDataSet = arrayListOf<IBarDataSet>(dataSet)
        val barData = BarData(iDataSet)
        barData.barWidth = 0.4f

        binding.mypageStatisticsWeekChartBc.run {
            data = barData
            setFitBars(true)
            invalidate()
        }
    }

    private fun setMonthCountChartData(monthCount: List<Int>) {
        // 그래프에 엔트리 넣고, 색깔 리스트 설정
        val entries = arrayListOf<Entry>()
        val colors = arrayListOf<Int>()

        for (idx in monthCount.indices) {
            entries.add(Entry((idx + 1).toFloat(), monthCount[idx].toFloat()))
            if (monthCount.size - 1 == idx) {
                colors.add(requireContext().getColor(R.color.secondary))
            } else {
                colors.add(requireContext().getColor(R.color.primary))
            }
        }

        // 라인 생성해서 엔트리 적용
        val dataSet = LineDataSet(entries, "DataSet")
        dataSet.lineWidth = 7f
        dataSet.circleRadius = 8f
        dataSet.circleHoleRadius = 3f
        dataSet.color = requireContext().getColor(R.color.primary)
        dataSet.circleColors = colors
        dataSet.setDrawValues(false)

        val iDataSet = arrayListOf<ILineDataSet>(dataSet)
        val lineData = LineData(iDataSet)

        binding.mypageStatisticsMonthCountChartLc.run {
            data = lineData
            invalidate()
        }
    }

    private fun setMonthRateChartData(monthRate: List<Int>) {
        // 그래프에 엔트리 넣고, 색깔 리스트 설정
        val entries = arrayListOf<BarEntry>()
        val colors = arrayListOf<Int>()

        for (idx in monthRate.indices) {
            entries.add(BarEntry((idx + 1).toFloat(), monthRate[idx].toFloat()))
            if (idx == 0) {
                colors.add(requireContext().getColor(R.color.primary_dark))
            } else if (idx == monthRate.size - 1) {
                colors.add(requireContext().getColor(R.color.secondary))
            } else {
                colors.add(requireContext().getColor(R.color.primary))
            }
        }

        // 막대 생성 해서 엔트리 적용
        val dataSet = BarDataSet(entries, "DataSet")
        dataSet.colors = colors
        dataSet.setDrawValues(false)

        val iDataSet = arrayListOf<IBarDataSet>(dataSet)
        val barData = BarData(iDataSet)
        barData.barWidth = 0.4f

        binding.mypageStatisticsMonthRateChartBc.run {
            data = barData
            setFitBars(true)
            invalidate()
        }
    }

    private fun getRecentMonths(isRate: Boolean): ArrayList<String> {
        val currentMonth = LocalDate.now()
        val months = arrayListOf<String>()

        for (i in 6 downTo 1) {
            val month = currentMonth.minusMonths(i.toLong())
            months.add("${month.monthValue}월")
        }
        months.add("이번달")

        // 월별 달성률 차트이면
        if (isRate) {
            months[0] = "평균"
        }

        return months
    }

    private fun convertToWalkTimeSlotToStr(slot: Int): String {
        return when (slot) {
            1 -> "이른 오전"
            2 -> "늦은 오전"
            3 -> "이른 오후"
            4 -> "늦은 오후"
            5 -> "밤"
            6 -> "새벽"
            7 -> "매번 다름"
            else -> ""
        }
    }

    private fun getMostWalkDay(list: List<String>): String {
        return if (list.size == 1) {
            list[0] + "요일"
        } else {
            list.joinToString(", ")
        }
    }

    inner class XAxisFormatter(private val labels: ArrayList<String>) : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return labels.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }

    override fun onMyPageLoading() {
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.mypageLoadingPb.visibility = View.VISIBLE
            })
        }
    }

    override fun onMyPageSuccess(achieveDetailResult: AchieveDetailResult) {
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.mypageLoadingPb.visibility = View.GONE

                setAchieveDetailResult(achieveDetailResult)
            })
        }
    }

    override fun onUserSuccess(user: User) {
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.mypageNickNameTv.text = user.nickname

                if (user.badgeUrl==null)    //대표 뱃지가 없을 경우
                    binding.mypageRepBadgeIv.setImageResource(R.drawable.ic_no_badge)
                else    //대표 뱃지가 있을 경우
                    binding.mypageRepBadgeIv.loadSvg(requireContext(), user.badgeUrl)
            })
        }
    }

    override fun onMyPageFailure(code: Int, message: String) {
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                if (!isNetworkAvailable(requireContext())) {
                    showSnackBar(getString(R.string.error_network))
                } else {
                    showSnackBar(getString(R.string.error_api_fail))
                }
            })
        }
    }

    private fun showSnackBar(errorMessage: String) {
        Snackbar.make(
            requireView(),
            errorMessage,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(getString(R.string.action_retry)) {
            // 유저 API 오류
            UserService.getUser(this)
            // 통계 API 오류
            AchieveService.getInfoDetail(this)
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 다른 Fragment 갈 때
        isFromFragment = true

        jobs.map {
            it.cancel()
        }
    }
}