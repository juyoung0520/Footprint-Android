package com.footprint.footprint.ui.main.mypage

import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentMypageBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.classes.custom.CustomBarChartRender
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class MyPageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {

    override fun initAfterBinding() {
        setBinding()

        setBarCharts()
        setLineChart()
    }

    private fun setBinding() {
        binding.mypageTodayPb.progress = 80
        binding.mypageMonthPb.progress = 80

        val spanColorPrimary =
            ForegroundColorSpan(requireContext().getColor(R.color.primary))
        val spanColorSecondary =
            ForegroundColorSpan(requireContext().getColor(R.color.secondary))

        val week = "3"
        binding.mypageGoalWeekTv.text =
            getSpannableString(binding.mypageGoalWeekTv.text, week, 2, spanColorPrimary)

        val minute = "20"
        binding.mypageGoalDayTv.text =
            getSpannableString(binding.mypageGoalDayTv.text, minute, 3, spanColorPrimary)

        val res1 = "월요일"
        binding.mypageStatisticsWeekResultTv.text = getSpannableString(
            binding.mypageStatisticsWeekResultTv.text, res1, 3, spanColorSecondary
        )

        val res2 = "20"
        binding.mypageStatisticsMonthCountResultTv.text = getSpannableString(
            binding.mypageStatisticsMonthCountResultTv.text,
            res2,
            5,
            spanColorSecondary
        )

        val res3 = "56"
        binding.mypageStatisticsMonthRateResultTv.text = getSpannableString(
            binding.mypageStatisticsMonthRateResultTv.text,
            res3,
            5,
            spanColorSecondary
        )

        binding.mypageGoalRightIv.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_badgeFragment)
        }
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

    private fun setBarCharts() {
        // week 그래프 설정
        initCharts(binding.mypageStatisticsWeekChartBc)

        binding.mypageStatisticsWeekChartBc.apply {
            xAxis.apply {
                textColor = requireContext().getColor(R.color.black)
                valueFormatter = XAxisFormatter(arrayListOf<String>("일", "월", "화", "수", "목", "금", "토"))
            }
        }
        // week 그래프 데이터 설정
        setWeekChartData()

        // 월별 달성률 그래프 설정
        initCharts(binding.mypageStatisticsMonthRateChartBc)

        binding.mypageStatisticsMonthRateChartBc.apply {
            xAxis.apply {
                textColor = requireContext().getColor(R.color.black_light)
                valueFormatter = XAxisFormatter(getRecentMonths(true))
            }
        }

        //월별 달성률 그래프 데이터 설정
        setMonthRateChartData()
    }

    private fun initCharts(chart: BarChart) {
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

    private fun setLineChart() {
        val colorGray = requireContext().getColor(R.color.white_caption)

        binding.mypageStatisticsMonthCountChartLc.apply {
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

        // 월별 기록 횟수 데이터 설정
        setMonthCountChartData()
    }

    private fun setWeekChartData() {
        // 값
        val weekRate = arrayListOf(0f, 40f, 50f, 100f, 50f, 20f, 10f)
        val maxIdx = weekRate.indexOf(weekRate.maxOrNull())

        // 그래프에 엔트리 넣고, 색깔 리스트 설정
        val entries = arrayListOf<BarEntry>()
        val colors = arrayListOf<Int>()

        for (idx in 0 until weekRate.size) {
            entries.add(BarEntry((idx + 1).toFloat(), weekRate[idx]))
            if (maxIdx == idx) {
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

    private fun setMonthCountChartData() {
        // 값
        val monthCount = arrayListOf(20f, 40f, 40f, 100f, 50f, 20f, 0f)

        // 그래프에 엔트리 넣고, 색깔 리스트 설정
        val entries = arrayListOf<Entry>()
        val colors = arrayListOf<Int>()

        for (idx in 0 until monthCount.size) {
            entries.add(Entry((idx + 1).toFloat(), monthCount[idx]))
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

    private fun setMonthRateChartData() {
        // 값
        val monthRate = arrayListOf(20f, 40f, 40f, 80f, 50f, 20f, 10f)

        // 그래프에 엔트리 넣고, 색깔 리스트 설정
        val entries = arrayListOf<BarEntry>()
        val colors = arrayListOf<Int>()

        for (idx in 0 until monthRate.size) {
            entries.add(BarEntry((idx + 1).toFloat(), monthRate[idx]))
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

    inner class XAxisFormatter(private val labels: ArrayList<String>) : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return labels.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }
}