package com.footprint.footprint.ui.main.home

import androidx.navigation.fragment.findNavController
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.main.MainActivity

class HomeFragment(): BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun initAfterBinding() {
        binding.homeContentTv.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.startNextActivity(WalkActivity::class.java)
        }
    }
}