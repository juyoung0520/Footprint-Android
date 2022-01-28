package com.footprint.footprint.ui.register

import android.graphics.Color
import android.util.Log
import android.view.WindowManager
import com.footprint.footprint.data.model.User
import com.footprint.footprint.databinding.ActivityRegisterBinding
import com.footprint.footprint.ui.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator


class RegisterActivity : BaseActivity<ActivityRegisterBinding>(ActivityRegisterBinding::inflate) {
    private var newUser: User = User()

    override fun initAfterBinding() {
        //TB, VP 세팅
        initVP()

        //soft Keyboard Up
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));


        //SignIn Activity -> User 받아오기
        if (intent.hasExtra("user")) {
            newUser = intent.getSerializableExtra("user") as User
            Log.d("REGISTER/USER", newUser.toString())
        }

    }


    /*TabLayout & ViewPager 세팅*/
    private fun initVP() {
        val registerVPAdapter = RegisterViewpagerAdapter(this)
        binding.registerVp.adapter = registerVPAdapter
        binding.registerVp.isUserInputEnabled = false
        TabLayoutMediator(binding.registerTb, binding.registerVp) { tab, position ->
            tab.text = (position + 1).toString()
            tab.view.isClickable = false
        }.attach()
    }

    /*뷰페이저 이동*/
    fun changeNextFragment(user: User) {
        Log.d("CHANGE", "OK")
        val current = binding.registerVp.currentItem
        if (current == 0) {
            //Info 프래그먼트: 1. Goal 프래그먼트로 넘기기 2. user 정보 업데이트
            binding.registerVp.setCurrentItem(1, true)

            newUser.nickname = user.nickname
            newUser.gender = user.gender
            newUser.birth = user.birth
            newUser.height = user.height
            newUser.weight = user.weight

            Log.d("REGISTER/USER", newUser.toString())
        }
    }
}