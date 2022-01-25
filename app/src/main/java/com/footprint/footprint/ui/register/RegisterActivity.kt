package com.footprint.footprint.ui.register

import android.content.Context
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.footprint.footprint.databinding.ActivityRegisterBinding
import com.footprint.footprint.ui.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class RegisterActivity : BaseActivity<ActivityRegisterBinding>(ActivityRegisterBinding::inflate) {
    override fun initAfterBinding() {

        //VP & TB 세팅
        val registerVPAdapter = RegisterViewpagerAdapter(this)
        binding.registerVp.adapter = registerVPAdapter
        //binding.registerVp.isUserInputEnabled = false

        TabLayoutMediator(binding.registerTb, binding.registerVp) { tab, position ->
            tab.text = (position + 1).toString()
        }.attach()

        //상단바 높이
        val statusbarHeight = getStatusBarHeightDP(this)
        binding.registerTopLaytout.setPadding(
            0,
            statusbarHeight - (statusbarHeight / 4),
            0,
            statusbarHeight / 4
        )
        Log.d(
            "STATUSBAR",
            "상단바 높이: ${statusbarHeight} margintop: ${statusbarHeight - (statusbarHeight / 4)} marginBottom: ${statusbarHeight / 4}"
        )

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    //상단바 높이 구하기
    fun getStatusBarHeightDP(context: Context): Int {
        var result = 0
        val resourceId: Int =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimension(resourceId).toInt()
        }
        return result
    }

    //dp to px 변환 함수 (params)
    private fun dp2px(density: Float, dp: Int): Int {
        return Math.round(dp.toFloat() * density)
    }
}