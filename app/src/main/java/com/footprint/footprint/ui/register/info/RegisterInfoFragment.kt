package com.footprint.footprint.ui.register.info

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentRegisterInfoBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.KeyboardVisibilityUtils

import android.view.View.OnFocusChangeListener
import com.footprint.footprint.data.model.User
import java.lang.Integer.parseInt


class RegisterInfoFragment() :
    BaseFragment<FragmentRegisterInfoBinding>(FragmentRegisterInfoBinding::inflate) {
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var newUser:User
    private var gender: String = "null"

    override fun initAfterBinding() {
        nicknameEt()
        genderInput()
        birthEt()
        weightEt()
        heightEt()

        keyboardUp()
    }


    private fun nicknameEt() {
        val nicknameEt = binding.registerInfoNicknameEt
        nicknameEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Focus
                nicknameEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
            } else {
                if (nicknameEt.text.isEmpty())
                    nicknameEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))
            }
        }
    }

    private fun genderInput(){
        val radioF = binding.registerInfoGenderFemaleBtn
        val radioM = binding.registerInfoGenderMaleBtn
        val radioN = binding.registerInfoGenderNoneBtn

        if(radioF.isChecked) gender = "female"
        if(radioM.isChecked) gender = "male"
        if(radioN.isChecked) gender = "null"
    }

    private fun birthEt() {
        val birthEt = binding.registerInfoBirthEt
        birthEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Focus
                birthEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
            } else {
                if (birthEt.text.isEmpty())
                    birthEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))
            }
        }
    }

    private fun heightEt() {
        val heightEt = binding.registerInfoHeightEt
        val heightUnit = binding.registerInfoHeightUnitTv
        heightEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Focus
                heightEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
                heightUnit.setTextColor(resources.getColor(R.color.primary))
            } else {
                if (heightEt.text.isEmpty())
                    heightEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))

            }
        }
    }

    private fun weightEt() {
        val weightEt = binding.registerInfoWeightEt
        val weightUnit = binding.registerInfoWeightUnitTv
        weightEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Focus
                weightEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
                weightUnit.setTextColor(resources.getColor(R.color.primary))
            } else {
                if (weightEt.text.isEmpty())
                    weightEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))
            }
        }
    }

    /*Soft Keyboard*/
    private fun keyboardUp() {
        //requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().getWindow(),
            onShowKeyboard = { keyboardHeight ->
                binding.registerInfoScrollLayout.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
                Log.d("KEYBOARD", "Height = ${keyboardHeight}")
            })
    }

    private fun TextWatcher() {
//        val Et = binding.registerInfoBirthEt
//        Et.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable) {}
//
//            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
//        })
    }
}


