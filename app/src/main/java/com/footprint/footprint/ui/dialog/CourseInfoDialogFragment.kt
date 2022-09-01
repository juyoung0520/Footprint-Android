package com.footprint.footprint.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.FragmentCourseInfoDialogBinding
import com.footprint.footprint.domain.model.CourseInfoModel
import com.footprint.footprint.ui.adapter.CourseTagRVAdapter
import com.footprint.footprint.utils.DialogFragmentUtils
import com.footprint.footprint.utils.DialogFragmentUtils.dialogFragmentResizeWidth
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class CourseInfoDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentCourseInfoDialogBinding
    private lateinit var myCallbackListener: MyCallbackListener

    interface MyCallbackListener {
        fun showCourse()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseInfoDialogBinding.inflate(inflater, container, false)

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        setBinding()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialogFragmentResizeWidth(requireContext(), this, 0.9f)
    }

    private fun setBinding() {
        val argument = arguments?.getString("course")
        val course = Gson().fromJson(argument, CourseInfoModel::class.java)

        binding.courseInfoTitleTv.text = course.title
        binding.courseInfoDistanceTimeTv.text =
            String.format("${course.distance}km, 약 ${course.time}분")
        binding.courseInfoDescriptionTv.text = course.description

        val tagRVAdapter = CourseTagRVAdapter(course.tags)
        binding.courseInfoTagRv.adapter = tagRVAdapter

        binding.courseInfoCloseIv.setOnClickListener {
            dismiss()
        }

        binding.courseInfoShowCourseBtn.setOnClickListener {
            dismiss()
            myCallbackListener.showCourse()
        }
    }

    fun setMyCallbackListener(myCallbackListener: MyCallbackListener) {
        this.myCallbackListener = myCallbackListener
    }
}