package com.footprint.footprint.ui.main.mypage

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentMypageBinding
import com.footprint.footprint.ui.BaseFragment
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

        val entries = arrayListOf<BarEntry>(
            BarEntry(1f, 20f),
            BarEntry(2f, 40f),
            BarEntry(3f, 40f),
            BarEntry(4f, 80f),
            BarEntry(5f, 50f),
            BarEntry(6f, 20f),
            BarEntry(7f, 10f),
        )

        binding.mypageBarChartBc.apply {
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)
            setTouchEnabled(false)
            axisLeft.apply {
                axisMaximum = 101f
                axisMinimum = 0f
                granularity = 20f
                setDrawLabels(false) // y축 값
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = XAxisFormatter()
                granularity = 1f
                setDrawLabels(true) // x축 값
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
        }

        val set = BarDataSet(entries, "DataSet")
        set.color = requireContext().getColor(R.color.primary)
        set.setDrawValues(false)

        val dataSet = arrayListOf<IBarDataSet>(set)
        val data = BarData(dataSet)
        data.barWidth = 0.5f

        binding.mypageBarChartBc.run {
            this.data = data
            setFitBars(true)
            invalidate()
        }

        binding.mypageGoalLeftIv.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_badgeFragment)
        }

        //설정 버튼 클릭 리스너 -> SettingFragment 로 이동
        binding.mypageSearchIv.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_navigation)
        }

        binding.mypageTmpGoalTv.setOnClickListener { //이번달 목표 텍스트뷰 클릭 리스너 -> GoalThisMonthFragment 로 이동(임시)
            findNavController().navigate(R.id.action_mypageFragment_to_goalThisMonthFragment)
        }
    }

    private fun setBinding() {
        binding.mypageTodayPb.progress = 80
        binding.mypageMonthPb.progress = 80

        val spanColorPrimary =
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary))

        val spannableWeek = SpannableString(binding.mypageGoalWeekTv.text)
        spannableWeek.setSpan(
            spanColorPrimary,
            2,
            spannableWeek.length - 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.mypageGoalWeekTv.text = spannableWeek


        val spannableDay = SpannableString(binding.mypageGoalDayTv.text)
        spannableDay.setSpan(
            spanColorPrimary,
            3,
            spannableDay.length - 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.mypageGoalDayTv.text = spannableDay

        binding.mypageStatisticsResult2Tv.text = " 월요일"
    }

    inner class XAxisFormatter : ValueFormatter() {
        private val days = arrayOf("일", "월", "화", "수", "목", "금", "토")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }
}