package com.footprint.footprint.ui.error

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.footprint.footprint.databinding.ActivityErrorBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.utils.getJwt

class ErrorActivity : BaseActivity<ActivityErrorBinding>(ActivityErrorBinding::inflate) {
    companion object {
        const val BACK = -1111
        const val CONTACT = 1111
        const val SCREEN = "SCREEN"
    }

    private lateinit var screen: String

    override fun initAfterBinding() {
        binding.errorGohomeTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        // 뒤로가기
        binding.errorBackBtnIv.setOnClickListener{
            setResult(BACK)
            finish()
        }

        // 문의하기
        binding.errorContactusTv.setOnClickListener{
            sendMail()
            setResult(CONTACT)
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
        screen = intent.getStringExtra(SCREEN).toString()

        // Splash, SignIn, Register -> 홈으로 가기 X
        when(screen){
            "SplashActivity", "SignInActivity", "RegisterGoalFragment" -> binding.errorGohomeTv.visibility = View.GONE
        }
    }

    @SuppressLint("IntentReset")
    private fun sendMail(){
        val emailAddress = "mysteps.team@gmail.com"
        val title = "[문의하기] "
        val content = "1) 발생일시: " + "\n2) 사용 중인 기기 정보(버전): " + "\n3) 문의 내용: "

        val intent = Intent(Intent.ACTION_SENDTO)
            .apply {
                type = "text/plain"
                data = Uri.parse("mailto:")

                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
            }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "메일 전송하기"))
        } else {
          showToast("메일을 전송할 수 없습니다.")
        }
    }

}