package com.footprint.footprint.ui.main.home

import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment(): BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {


    override fun initAfterBinding() {
        binding.homeContentTv.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.startNextActivity(WalkActivity::class.java)
        }

        val tbTitle = arrayListOf("일별", "월별")
        val homeVPAdapter = HomeViewpagerAdapter(this)
        binding.homeDaymonthVp.adapter = homeVPAdapter

        TabLayoutMediator(binding.homeDaymonthTb, binding.homeDaymonthVp){ tab, position ->
            tab.text = tbTitle[position]
        }.attach()
    }

    /*private fun setMyClickListener() {
        binding.homeContentTv.setOnClickListener {
            startActivity(Intent(requireContext(), WalkAfterActivity::class.java))
        }
    }*/
}