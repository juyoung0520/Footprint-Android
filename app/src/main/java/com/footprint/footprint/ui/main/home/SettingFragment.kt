package com.footprint.footprint.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentSettingBinding
import com.footprint.footprint.ui.BaseFragment

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    override fun initAfterBinding() {
        setMyEventListener()
    }

    private fun setMyEventListener() {
        binding.settingBackIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.settingUpdateMyInfoNextIv.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_myInfoFragment)
        }
    }
}