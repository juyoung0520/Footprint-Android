package com.footprint.footprint.ui.main.course

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.FragmentMyBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.MyVPAdapter
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.viewmodel.RecommendedCourseViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyFragment : BaseFragment<FragmentMyBinding>(FragmentMyBinding::inflate) {
    private var currentItem: Int = -1   //현재 ViewPager 의 currentItem 을 저장하는 변수
    private lateinit var networkErrSb: Snackbar

    private lateinit var myVpAdapter: MyVPAdapter
    private lateinit var backPressedCallback: OnBackPressedCallback

    private val myViewModel: RecommendedCourseViewModel by viewModel()

    interface CoursesListener {
        fun observer(courses: List<CourseDTO>)
    }

    override fun initAfterBinding() {
        setBackPressedCallback()
        initVPAdapter()
        setMyEventListener()
        observe()
    }

    override fun onStop() {
        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
        this.currentItem = binding.myVp.currentItem //화면이 가려지면 현재 탭의 currentItem 을 저장한다.
        super.onStop()
    }

    override fun onDetach() {
        super.onDetach()
        backPressedCallback.remove()
    }

    //뒤로가기 콜백 리스너 설정
    private fun setBackPressedCallback() {
        backPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.myVp.currentItem==0)    //ViewPager 가 내 추천코스 화면에 있으면 찜한 목록으로 넘어가기
                    findNavController().popBackStack()
                else    //ViewPager 가 찜한 목록에 있으면 프래그먼트 종료
                    binding.myVp.currentItem = binding.myVp.currentItem - 1
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    //뷰페이저 설정
    private fun initVPAdapter() {
        myVpAdapter = MyVPAdapter(this)
        binding.myVp.adapter = myVpAdapter
        TabLayoutMediator(binding.myTl, binding.myVp) {tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.title_wish_list)
                1 -> tab.text = getString(R.string.title_my_recommended_course)
            }
        }.attach()

        binding.myVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.myLoadingPb.visibility = View.VISIBLE

                when (position) {
                    0 -> myViewModel.getMarkedCourses()
                    1 -> myViewModel.getRecommendedCourses()
                }
            }
        })

        if (this.currentItem!=-1)   //다른 화면에서 뒤로가기로 인해 돌아온 화면이면
            binding.myVp.currentItem = this.currentItem //맨 마지막 ViewPager 의 currentItem 으로 돌아가 있는다.
    }

    private fun setMyEventListener() {
        //뒤로가기 아이콘 이미지뷰 클릭 리스너
        binding.myBackIv.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observe() {
        myViewModel.markedCourses.observe(viewLifecycleOwner) {
            binding.myLoadingPb.visibility = View.INVISIBLE
            myVpAdapter.callObserver(binding.myVp.currentItem, it)
        }

        myViewModel.recommendedCourses.observe(viewLifecycleOwner) {
            binding.myLoadingPb.visibility = View.INVISIBLE
            myVpAdapter.callObserver(binding.myVp.currentItem, it)
        }

        myViewModel.mutableErrorType.observe(viewLifecycleOwner) {
            when (it) {
                ErrorType.NETWORK -> {
                    networkErrSb = Snackbar.make(
                        binding.root,
                        getString(R.string.error_network),
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("재시도") {
                        if (binding.myVp.currentItem == 0) myViewModel.getMarkedCourses()
                        else myViewModel.getRecommendedCourses()
                    }
                    networkErrSb.show()
                }
                ErrorType.UNKNOWN -> {
                    startErrorActivity("MyFragment")
                }
                else -> {}
            }
        }
    }

    fun hidePb() {
        binding.myLoadingPb.visibility = View.INVISIBLE
    }
}