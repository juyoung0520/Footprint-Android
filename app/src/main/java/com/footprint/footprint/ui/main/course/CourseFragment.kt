package com.footprint.footprint.ui.main.course

import com.footprint.footprint.databinding.FragmentCourseBinding
import com.footprint.footprint.ui.BaseFragment

class CourseFragment(): BaseFragment<FragmentCourseBinding>(FragmentCourseBinding::inflate) {

    override fun initAfterBinding() {
        Glide.with(this).load(R.raw.walking).into(binding.courseBackgroundIv)
        setMyClickListener()
    }

    private fun setMyClickListener() {
        binding.courseBackIv.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}