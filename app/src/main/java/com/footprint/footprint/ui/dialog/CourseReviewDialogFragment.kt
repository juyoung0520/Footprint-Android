package com.footprint.footprint.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.footprint.footprint.databinding.FragmentCourseReviewDialogBinding
import com.footprint.footprint.utils.LogUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CourseReviewDialogFragment : BottomSheetDialogFragment() {
    private lateinit var reviewCallback: ReviewCallback
    private var isGood = false

    interface ReviewCallback {
        fun review(isGood: Boolean)
    }

    private lateinit var binding: FragmentCourseReviewDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCourseReviewDialogBinding.inflate(inflater, container, false)

        binding.courseReviewGoodLl.setOnClickListener {
            isGood = true
            reviewCallback.review(isGood)
        }

        binding.courseReviewNextTv.setOnClickListener {
            isGood = false
            reviewCallback.review(isGood)
        }

        binding.courseReviewDeclarationReportTv.setOnClickListener {
            isGood = false
            reviewCallback.review(isGood)
        }

        return binding.root
    }

    fun setReviewCallback(callback: ReviewCallback) {
        reviewCallback = callback
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        reviewCallback.review(isGood)
    }
}