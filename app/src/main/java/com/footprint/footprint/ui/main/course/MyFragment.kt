package com.footprint.footprint.ui.main.course

import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentMyBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.MyVPAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MyFragment : BaseFragment<FragmentMyBinding>(FragmentMyBinding::inflate) {
    private var currentItem: Int = -1   //현재 ViewPager 의 currentItem 을 저장하는 변수

    private lateinit var myVpAdapter: MyVPAdapter
    private lateinit var backPressedCallback: OnBackPressedCallback

    override fun initAfterBinding() {
        setBackPressedCallback()
        initVPAdapter()
        setMyEventListener()
    }

    override fun onStop() {
        super.onStop()
        this.currentItem = binding.myVp.currentItem //화면이 가려지면 현재 탭의 currentItem 을 저장한다.
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

        if (this.currentItem!=-1)   //다른 화면에서 뒤로가기로 인해 돌아온 화면이면
            binding.myVp.currentItem = this.currentItem //맨 마지막 ViewPager 의 currentItem 으로 돌아가 있는다.
    }

    private fun setMyEventListener() {
        //뒤로가기 아이콘 이미지뷰 클릭 리스너
        binding.myBackIv.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}