package com.footprint.footprint.ui.main.course

import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentCourseSelectBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.LogUtils
import com.innovattic.rangeseekbar.RangeSeekBar

class CourseSelectFragment : BaseFragment<FragmentCourseSelectBinding>(FragmentCourseSelectBinding::inflate), RangeSeekBar.SeekBarChangeListener {
    override fun initAfterBinding() {
        setMyEventListener()
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

    private fun setMyEventListener() {
        binding.courseSelectRsb.seekBarChangeListener = this
        binding.courseSelectNextTv.setOnClickListener {
            findNavController().navigate(R.id.action_courseSelectFragment_to_courseShareFragment)
        }
    }
}

