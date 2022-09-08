package com.footprint.footprint.ui.main.course

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.FragmentRecommendedCourseBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseListRVAdapter
import com.footprint.footprint.ui.adapter.CourseRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.LogUtils
import com.google.android.material.snackbar.Snackbar

class RecommendedCourseFragment : BaseFragment<FragmentRecommendedCourseBinding>(FragmentRecommendedCourseBinding::inflate), MyFragment.CoursesListener {
    private var selectedPosition: Int = -1

    private lateinit var myBottomSheetDialogFragment: MyBottomSheetDialogFragment
    private lateinit var courseRVAdapter: CourseListRVAdapter
    private lateinit var actionFrag: ActionDialogFragment

    override fun initAfterBinding() {
        initBottomSheetDialogFragment()
        initRVAdapter()
        initActionFrag()
        setMyEventListener()
    }

    override fun onPause() {
        super.onPause()

        if (::myBottomSheetDialogFragment.isInitialized && myBottomSheetDialogFragment.isAdded)
            myBottomSheetDialogFragment.dismiss()
    }

    private fun initBottomSheetDialogFragment() {
        myBottomSheetDialogFragment = MyBottomSheetDialogFragment()
        myBottomSheetDialogFragment.setMyCallback(object : MyBottomSheetDialogFragment.Callback {
            override fun show() {
                showToast("SHOW")
            }

            override fun update() {
                showToast("UPDATE")
                //CourseShareActivity 로 이동
            }

            override fun delete() {
                val bundle: Bundle = Bundle()
                bundle.putString("msg", "정말 ‘OO코스'를 삭제할까요?")
                bundle.putString("left", getString(R.string.action_cancel))
                bundle.putString("right", getString(R.string.action_delete))

                actionFrag.arguments = bundle
                actionFrag.show(requireActivity().supportFragmentManager, null)
            }
        })
    }

    private fun initRVAdapter() {
        courseRVAdapter = CourseListRVAdapter(requireContext())
        courseRVAdapter.setMyClickListener(object : CourseListRVAdapter.CourseClickListener {
            override fun onClick(course: CourseDTO, position: Int) {
                selectedPosition = position
                myBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, null)
            }

            override fun markCourse(courseIdx: String) {}
        })
        binding.recommendedCourseRv.adapter = courseRVAdapter
    }

    private fun initActionFrag() {
        actionFrag = ActionDialogFragment()
        actionFrag.setMyDialogCallback(object: ActionDialogFragment.MyDialogCallback {
            override fun leftAction(action: String) {
            }

            override fun rightAction(action: String) {
                courseRVAdapter.removeData(selectedPosition)    //데이터 삭제
            }
        })
    }

    private fun setMyEventListener() {
        binding.recommendedCourseFac.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_courseSelectActivity)
        }
    }

    override fun observer(courses: List<CourseDTO>) {
        LogUtils.d("courses-recommend", courses.toString())
        courseRVAdapter.addAll(courses as ArrayList<CourseDTO>)
    }
}