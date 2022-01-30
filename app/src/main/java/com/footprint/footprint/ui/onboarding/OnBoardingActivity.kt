package com.footprint.footprint.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityOnboardingBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.signin.SigninActivity
import com.footprint.footprint.utils.saveOnboarding
import com.google.android.material.tabs.TabLayoutMediator


class OnBoardingActivity :
    BaseActivity<ActivityOnboardingBinding>(ActivityOnboardingBinding::inflate) {

    override fun initAfterBinding() {
        initVP()
        initBtn()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ViewPager 이벤트 리스너
        binding.onboardingVp.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                changeUI(position)
            }
        })

    }


    /*Function*/

    /*초기 준비: 뷰페이저, 버튼(next, skip)*/
    private fun initVP() {
        val onboardingVPAdapter = OnboardingViewpagerAdapter(this)
        binding.onboardingVp.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.onboardingVp.adapter = onboardingVPAdapter
        TabLayoutMediator(binding.onboardingTb, binding.onboardingVp) { tab, position ->
            tab.view.isClickable = false
        }.attach()
    }

    private fun initBtn() {
        //액션 버튼
        binding.onboardingActionBtn.setOnClickListener {
            val current = binding.onboardingVp.currentItem
            changeNextFragment(current)
        }

        //skip 버튼
        binding.onboardingSkipTv.setOnClickListener {
            saveOnboarding(this, true)
            startNextActivity(SigninActivity::class.java)
            finish()
        }
    }

    /*뷰페이저 이동*/
    private fun changeNextFragment(current: Int) {
        if (current < 3) { // 0, 1, 2 -> 다음 프래그먼트로
            binding.onboardingVp.setCurrentItem(current + 1, true)
        } else if (current == 3) { // 3 -> 로그인 액티비티로
            //1. spf 저장
            saveOnboarding(this, true)

            //2. -> Signin Activity
            startNextActivity(SigninActivity::class.java)
        }
    }

    /*현재 위치에 따라 skip 버튼, action 버튼 UI 변경*/
    private fun changeUI(position: Int) {
        if (position == 3) {
            binding.onboardingActionBtn.setText(R.string.action_start)
            binding.onboardingSkipTv.visibility = View.GONE
        } else {
            binding.onboardingActionBtn.setText(R.string.action_next)
            binding.onboardingSkipTv.visibility = View.VISIBLE
        }
    }
}