package com.footprint.footprint.ui.main.course

import android.view.ViewGroup
import com.footprint.footprint.databinding.FragmentCourseSelectBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.convertDpToPx
import com.innovattic.rangeseekbar.RangeSeekBar
import java.lang.Math.round


class CourseSelectFragment : BaseFragment<FragmentCourseSelectBinding>(FragmentCourseSelectBinding::inflate), RangeSeekBar.SeekBarChangeListener {
    override fun initAfterBinding() {
        binding.courseSelectRsb.seekBarChangeListener = this
    }

    override fun onStartedSeeking() {
    }

    override fun onStoppedSeeking() {
    }

    override fun onValueChanged(minThumbValue: Int, maxThumbValue: Int) {
        val width = binding.courseSelectRsb.width
        val marginStart = width * (minThumbValue / 100.0)
        val marginEnd = width * ((100 - maxThumbValue) / 100.0)
        LogUtils.d("CourseSelectFragment", "onValueChanged ${minThumbValue / 100.0} width: $width, minThumbValue: $minThumbValue, marginStart: $marginStart, maxThumbValue: $maxThumbValue")

        val param = binding.courseSelectBarPrimaryView.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(marginStart.toInt(),0, marginEnd.toInt(),0)
        binding.courseSelectBarPrimaryView.layoutParams = param
    }
}

