package com.footprint.footprint.ui.main.home

import com.dinuscxj.progressbar.CircleProgressBar
import com.footprint.footprint.databinding.FragmentHomeDayBinding
import com.footprint.footprint.ui.BaseFragment

class HomeDayFragment:  BaseFragment<FragmentHomeDayBinding>(FragmentHomeDayBinding::inflate) , CircleProgressBar.ProgressFormatter {

    override fun initAfterBinding() {
             var circleProgressBar = binding.homeDayPb
             circleProgressBar.progress = 80

    }

    override fun format(progress: Int, max: Int): CharSequence {
        return String.format("%d%", (progress.toFloat()/max.toFloat()*100).toInt())
    }
}