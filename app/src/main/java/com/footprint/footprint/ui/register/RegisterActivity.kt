package com.footprint.footprint.ui.register

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.adapters.ViewBindingAdapter
import com.footprint.footprint.R
import com.footprint.footprint.data.model.User
import com.footprint.footprint.databinding.ActivityRegisterBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.register.info.RegisterInfoFragment
import com.footprint.footprint.utils.KeyboardVisibilityUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import androidx.viewpager.widget.ViewPager


class RegisterActivity : BaseActivity<ActivityRegisterBinding>(ActivityRegisterBinding::inflate) {
    //private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private var newUser: User = User()

    override fun initAfterBinding() {
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


    /*뷰페이저*/
    private fun initVP() {
        //VP & TB 세팅
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

            Log.d("ACTIVITY", newUser.toString())
        }
    }
}