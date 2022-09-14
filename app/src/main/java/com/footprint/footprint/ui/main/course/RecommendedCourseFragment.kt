package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.FragmentRecommendedCourseBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.CourseListRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.viewmodel.RecommendedCourseViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecommendedCourseFragment : BaseFragment<FragmentRecommendedCourseBinding>(FragmentRecommendedCourseBinding::inflate), MyFragment.CoursesListener {
    private val vm: RecommendedCourseViewModel by viewModel()

    private var markedCourseIdx: Int = -1

    private lateinit var selectedCourse: CourseDTO
    private lateinit var myBottomSheetDialogFragment: MyBottomSheetDialogFragment
    private lateinit var courseRVAdapter: CourseListRVAdapter
    private lateinit var actionFrag: ActionDialogFragment
    private lateinit var networkErrSb: Snackbar

    override fun initAfterBinding() {
        initBottomSheetDialogFragment()
        initActionFrag()
        setMyEventListener()
        observe()

        vm.getRecommendedCourses()
    }

    override fun onPause() {
        super.onPause()

        if (::myBottomSheetDialogFragment.isInitialized && myBottomSheetDialogFragment.isAdded)
            myBottomSheetDialogFragment.dismiss()
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }

    private fun initBottomSheetDialogFragment() {
        myBottomSheetDialogFragment = MyBottomSheetDialogFragment()
        myBottomSheetDialogFragment.setMyCallback(object : MyBottomSheetDialogFragment.Callback {
            override fun show() {
                val intent = Intent(requireContext(), CourseDetailActivity::class.java)
                intent.putExtra("course", Gson().toJson(selectedCourse))
                startActivity(intent)
            }

            override fun update() {
                val intent = Intent(requireContext(), CourseShareActivity::class.java)
                intent.putExtra("courseName", selectedCourse.courseName)
                startActivity(intent)
            }

            override fun delete() {
                val bundle: Bundle = Bundle()
                bundle.putString("msg", "정말 ‘${selectedCourse.courseName}'를 삭제할까요?")
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
                selectedCourse = course
                myBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, null)
            }

            override fun markCourse(courseIdx: Int) {
                markedCourseIdx = courseIdx
                vm.markCourse(courseIdx)
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
                vm.deleteCourse(selectedCourse.courseIdx)
            }
        })
    }

    private fun setMyEventListener() {
        binding.recommendedCourseFac.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_courseSelectActivity)
        }
    }

    private fun observe() {
        (requireParentFragment() as MyFragment).hidePb()

        vm.mutableErrorType.observe(this, Observer {
            when (it) {
                ErrorType.NETWORK -> {  //네트워크 에러
                    when (vm.getErrorType()) {
                        "getMarkedCourses",  "deleteCourse", "markCourse" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_LONG)
                        "getRecommendedCourses" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) { vm.getRecommendedCourses() }
                    }

                    networkErrSb.show()
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER, ErrorType.S3 -> {
                    startErrorActivity("RecommendedCourseFragment")
                }
            }
        })

        vm.deleteResultCode.observe(viewLifecycleOwner, Observer {
            if (it==1000) {
                courseRVAdapter.removeData(selectedCourse)    //데이터 삭제
            }
        })

        vm.markCourseResultCode.observe(viewLifecycleOwner, Observer {
            if (it==1000) {
                courseRVAdapter.changeUserCourseMark(markedCourseIdx)
            } else {
                showToast(getString(R.string.error_api_fail))
            }
        })
    }

    override fun observer(courses: List<CourseDTO>) {
        initRVAdapter()
        courseRVAdapter.addAll(courses as ArrayList<CourseDTO>)
    }
}