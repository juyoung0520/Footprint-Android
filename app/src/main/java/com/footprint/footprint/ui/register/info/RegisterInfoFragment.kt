package com.footprint.footprint.ui.register.info

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentRegisterInfoBinding
import com.footprint.footprint.ui.BaseFragment

class RegisterInfoFragment() :
    BaseFragment<FragmentRegisterInfoBinding>(FragmentRegisterInfoBinding::inflate) {
    override fun initAfterBinding() {

        //nickname
        val nicknameEt = binding.registerInfoNicknameEt
        nicknameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                if(nicknameEt.text.length > 6){
                    //nicknameEt.backgroundTintList =
                    nicknameEt.isEnabled = false
                    nicknameEt.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.primary))
                }
            }
            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                nicknameEt.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.primary))
            }
        })


    }
}


