package com.footprint.footprint.ui.agree

import androidx.navigation.findNavController
import com.footprint.footprint.databinding.ActivityAgreeBinding
import com.footprint.footprint.ui.BaseActivity

class AgreeActivity : BaseActivity<ActivityAgreeBinding>(ActivityAgreeBinding::inflate) {
    override fun initAfterBinding() {

        //back 버튼
        binding.agreeBackIv.setOnClickListener {
            if (!findNavController(binding.navHostFragmentContainer.id).popBackStack()) {
                finish()
            }
        }
    }
}