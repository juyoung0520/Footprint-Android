package com.footprint.footprint.ui.main.course

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentRecommendedCourseBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment

class RecommendedCourseFragment : BaseFragment<FragmentRecommendedCourseBinding>(FragmentRecommendedCourseBinding::inflate) {
    private var selectedPosition: Int = -1

    private lateinit var myBottomSheetDialogFragment: MyBottomSheetDialogFragment
    private lateinit var courseRVAdapter: CourseRVAdapter
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
        courseRVAdapter = CourseRVAdapter()
        courseRVAdapter.setMyClickListener(object : CourseRVAdapter.MyClickListener {
            override fun onClick(position: Int) {
                selectedPosition = position
                myBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, null)
            }
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
}