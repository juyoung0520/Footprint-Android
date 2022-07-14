package com.footprint.footprint.ui.error

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import com.footprint.footprint.databinding.ActivityErrorBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.utils.getJwt

class ErrorActivity : BaseActivity<ActivityErrorBinding>(ActivityErrorBinding::inflate) {
    companion object {
        const val RETRY = 2222
        const val SCREEN = "SCREEN"
    }

    override fun initAfterBinding() {
        binding.errorGohomeTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        // 뒤로가기
        binding.errorBackBtnIv.setOnClickListener{
            finish()
        }

        // 재시도
        binding.errorRetryBtn.setOnClickListener{
             setResult(RETRY, intent)
             finish()
        }

        // 홈으로 가기
        binding.errorGohomeTv.setOnClickListener{
            finishAffinity()
            startNextActivity(MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash, SignIn, Register -> 홈으로 가기 X
        when(intent.getStringExtra(SCREEN)){
            "SplashActivity", "SignInActivity", "RegisterActivity" -> binding.errorGohomeTv.visibility = View.GONE
        }

    }
}