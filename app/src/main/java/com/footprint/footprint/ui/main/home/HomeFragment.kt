package com.footprint.footprint.ui.main.home

import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment

class HomeFragment(): BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun initAfterBinding() {
        binding.homeContentTv.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.startNextActivity(WalkActivity::class.java)
        }
    }

    /*private fun setMyClickListener() {
        binding.homeContentTv.setOnClickListener {
            startActivity(Intent(requireContext(), WalkAfterActivity::class.java))
        }
    }*/
}