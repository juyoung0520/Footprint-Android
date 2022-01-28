package com.footprint.footprint.ui.main.home

import com.footprint.footprint.databinding.FragmentMyInfoBinding
import com.footprint.footprint.ui.BaseFragment

class MyInfoFragment : BaseFragment<FragmentMyInfoBinding>(FragmentMyInfoBinding::inflate) {
    override fun initAfterBinding() {
        setMyClickListener()
    }

    private fun setMyClickListener() {
        binding.myInfoBackIv.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}