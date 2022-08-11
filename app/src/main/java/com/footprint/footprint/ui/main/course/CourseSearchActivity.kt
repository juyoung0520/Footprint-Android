package com.footprint.footprint.ui.main.course

import com.footprint.footprint.databinding.ActivityCourseSearchBinding
import com.footprint.footprint.ui.BaseActivity
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class CourseSearchActivity: BaseActivity<ActivityCourseSearchBinding>(ActivityCourseSearchBinding::inflate){
    override fun initAfterBinding() {
        setClickListener()
    }

    private fun setClickListener(){
        binding.courseSearchBackIv.setOnClickListener {
            finish()
        }

        binding.courseSearchBarTv.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        binding.courseSearchSlidingUpPanelLayout.apply {
            if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED || panelState == SlidingUpPanelLayout.PanelState.ANCHORED)   //SlidingUpPanelLayout 이 위로 올라가 있으면 아래로 내리기
                panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            else
                super.onBackPressed()
        }
    }


}