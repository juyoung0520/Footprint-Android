package com.footprint.footprint.ui.main.course

import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentCourseBinding
import com.footprint.footprint.ui.BaseFragment

class CourseFragment(): BaseFragment<FragmentCourseBinding>(FragmentCourseBinding::inflate) {

    override fun initAfterBinding() {
        Glide.with(this).load(R.raw.walking).into(binding.courseBackgroundIv)
    }

}