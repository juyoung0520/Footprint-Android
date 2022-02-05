package com.footprint.footprint.ui.main.mypage

import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentMypageBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.classes.custom.CustomBarChartRender
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

class MyPageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {

    override fun initAfterBinding() {
        setBinding()

        initWeekGraph()
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

        binding.mypageGoalRightIv.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_badgeFragment)
        }

        binding.mypageBackIv.setOnClickListener {

        }
    }

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

    private fun initWeekGraph() {
        val colorGray = requireContext().getColor(R.color.white_caption)

        // 커스텀 막대 radius 설정
        val barChartRender =
            CustomBarChartRender(
                binding.mypageBarChartBc,
                binding.mypageBarChartBc.animator,
                binding.mypageBarChartBc.viewPortHandler
            )
        barChartRender.setRadius(30)

        // 그래프 설정
        binding.mypageBarChartBc.apply {
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)
            setTouchEnabled(false)
            setPadding(16, 10, 16, 10)
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
                textColor = requireContext().getColor(R.color.black_dark)
                textSize = 12f
                typeface = resources.getFont(R.font.namusquareround_bold)
                valueFormatter = XAxisFormatter()
                granularity = 1f
                setDrawLabels(true) // x축 값
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
        }

        // 데이터 설정
        setWeekGraphData()
    }

    private fun setWeekGraphData() {
        // 값
        val weekRate = arrayListOf<Float>(20f, 40f, 40f, 80f, 50f, 20f, 10f)
        val maxIdx = weekRate.indexOf(weekRate.maxOrNull())

        val entries = arrayListOf<BarEntry>()
        val colors = arrayListOf<Int>()

        for (idx in 0 until weekRate.size) {
            entries.add(BarEntry((idx + 1).toFloat(), weekRate[idx]))
            if (maxIdx != idx) {
                colors.add(requireContext().getColor(R.color.primary))
            } else {
                colors.add(requireContext().getColor(R.color.secondary))
            }
        }

        val set = BarDataSet(entries, "DataSet")
        set.colors = colors
        set.setDrawValues(false)

        val dataSet = arrayListOf<IBarDataSet>(set)
        val data = BarData(dataSet)
        data.barWidth = 0.4f

        binding.mypageBarChartBc.run {
            this.data = data
            setFitBars(true)
            invalidate()
        }
    }

    inner class XAxisFormatter : ValueFormatter() {
        private val days = arrayOf("일", "월", "화", "수", "목", "금", "토")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }
}