package com.footprint.footprint.ui.onboarding

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityOnboardingBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.signin.SigninActivity
import com.footprint.footprint.utils.saveOnboarding
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardingActivity :
    BaseActivity<ActivityOnboardingBinding>(ActivityOnboardingBinding::inflate) {

    override fun initAfterBinding() {
        //Viewpager & TabLayout
        initVP()

        //버튼: skip, 다음
        initBtn()
    }

    /*초기 준비*/
    private fun initVP() {
        val onboardingVPAdapter = OnboardingViewpagerAdapter(this)
        binding.onboardingVp.isUserInputEnabled = false
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
            changeUI(current)
        }

        //skip 버튼
        binding.onboardingSkipTv.setOnClickListener {
            startNextActivity(SigninActivity::class.java)
            finish()
        }
    }

    /*뷰페이저 이동*/
    private fun changeNextFragment(current: Int) {
        if (current < 3) {
            binding.onboardingVp.setCurrentItem(current + 1, true)
        }else if(current == 3){
            //1. spf 저장
            saveOnboarding(this, true)

            //2. -> Signin Activity
            startNextActivity(SigninActivity::class.java)
        }
    }

    /*현재 위치에 따라 skip 버튼, action 버튼 UI 변경*/
    private fun changeUI(current: Int) {
        if(current == 2){
            // 버튼  -> 시작하기
            binding.onboardingActionBtn.setText(R.string.action_start)
            // skip -> 사라짐
            binding.onboardingSkipTv.visibility = View.GONE
        }
    }

}