package com.footprint.footprint.ui.register

import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.domain.model.InitUserModel
import com.footprint.footprint.databinding.ActivityRegisterBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.RegisterViewpagerAdapter
import com.footprint.footprint.ui.register.goal.RegisterGoalFragment
import com.footprint.footprint.ui.register.info.RegisterInfoFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.footprint.footprint.utils.LogUtils


class RegisterActivity : BaseActivity<ActivityRegisterBinding>(ActivityRegisterBinding::inflate) {
    private lateinit var registerVPAdapter: RegisterViewpagerAdapter

    private var newUser: InitUserModel = InitUserModel()

    override fun initAfterBinding() {
        //TB, VP 세팅
        if (!::registerVPAdapter.isInitialized)
            initVP()
    }

    override fun onBackPressed() {
        if (binding.registerVp.currentItem == 1)  //뷰페이저가 목표 설정 화면에 있으면 -> 정보 입력 화면으로 이동
            binding.registerVp.currentItem = 0
        else    //뷰페이저가 정보 입력 화면에 있으면 -> 뒤로가기(앱 종료)
            super.onBackPressed()
    }


    /*TabLayout & ViewPager 세팅*/
    private fun initVP() {
        registerVPAdapter = RegisterViewpagerAdapter(this)
        registerVPAdapter.setFragments(arrayListOf(RegisterInfoFragment(), RegisterGoalFragment()))
        binding.registerVp.adapter = registerVPAdapter
        binding.registerVp.isUserInputEnabled = false
        binding.registerVp.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        TabLayoutMediator(binding.registerTb, binding.registerVp) { tab, position ->
            tab.text = (position + 1).toString()
            tab.view.isClickable = false
        }.attach()
    }

    /*뷰페이저 이동*/
    fun changeNextFragment(user: InitUserModel) {
        LogUtils.d("CHANGE", "OK")
        val current = binding.registerVp.currentItem
        if (current == 0) {
            //Info 프래그먼트: 1. Goal 프래그먼트로 넘기기 2. user 정보 업데이트
            newUser.nickname = user.nickname
            newUser.gender = user.gender
            newUser.birth = user.birth
            newUser.height = user.height
            newUser.weight = user.weight

            registerVPAdapter.sendUserToGoalFrag(newUser)   //GoalFragment 로 newUser 전달
            binding.registerVp.setCurrentItem(1, true)  //GoalFragment 로 화면 이동

            LogUtils.d("REGISTER/USER", newUser.toString())
        }
    }
}