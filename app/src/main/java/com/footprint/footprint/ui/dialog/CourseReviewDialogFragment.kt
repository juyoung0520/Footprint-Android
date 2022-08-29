package com.footprint.footprint.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.footprint.footprint.databinding.FragmentCourseReviewDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CourseReviewDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCourseReviewDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCourseReviewDialogBinding.inflate(inflater, container, false)

        binding.courseReviewGoodLl.setOnClickListener {
            dismiss()
        }

        binding.courseReviewNextTv.setOnClickListener {
            dismiss()
        }

        binding.courseReviewDeclarationReportTv.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}