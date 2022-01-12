package com.footprint.footprint.ui.main.home

import com.dinuscxj.progressbar.CircleProgressBar
import com.footprint.footprint.databinding.FragmentHomeDayBinding
import com.footprint.footprint.ui.BaseFragment

class HomeDayFragment:  BaseFragment<FragmentHomeDayBinding>(FragmentHomeDayBinding::inflate){

    override fun initAfterBinding() {
             var circleProgressBar = binding.homeDayPb
             circleProgressBar.progress = 80
    }

}