package com.footprint.footprint.ui.signin

import com.footprint.footprint.databinding.ActivitySigninBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.main.home.WalkActivity

class SigninActivity: BaseActivity<ActivitySigninBinding>(ActivitySigninBinding::inflate) {

    override fun initAfterBinding() {
        binding.signinKakaologinBtnLayout.setOnClickListener {
            this.startNextActivity(MainActivity::class.java)
        }
    }
}