package com.footprint.footprint.ui.main.home

import com.footprint.footprint.databinding.ActivityWalkBinding
import com.footprint.footprint.ui.BaseActivity

class WalkActivity : BaseActivity<ActivityWalkBinding>(ActivityWalkBinding::inflate) {
    override fun initAfterBinding() {
        binding.walkCancelTv.setOnClickListener {
            finish()
        }
    }
}