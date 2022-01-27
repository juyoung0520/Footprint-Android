package com.footprint.footprint.ui.register.info

import android.content.Context
import android.content.Intent
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
import androidx.databinding.adapters.ViewBindingAdapter.setPadding
import com.footprint.footprint.data.model.User
import com.skydoves.balloon.*
import java.lang.Integer.parseInt
import com.google.android.material.internal.ViewUtils.dpToPx
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.skydoves.balloon.OnBalloonClickListener
import android.widget.Toast

import android.widget.RadioButton

import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.footprint.footprint.ui.register.RegisterActivity
import com.footprint.footprint.ui.main.MainActivity





class RegisterInfoFragment() :
    BaseFragment<FragmentRegisterInfoBinding>(FragmentRegisterInfoBinding::inflate) {
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    private lateinit var tmpUser: User
    private var newUser: User = User()
    private var isNicknameCorrect = false
    private var isGenderCorrect = false
    private val THIS_YEAR = 2022

    private lateinit var animation: Animation


    override fun initAfterBinding() {
        animation = AnimationUtils.loadAnimation(activity, R.anim.shake)

        /*각 EditText에 입력 받기 */
        nicknameEt()
        genderInput()
        birthEt()
        weightEt()
        heightEt()

        setHelpBallon()

        /*버튼 활성화 & 눌렀을 때*/
        binding.registerInfoActionBtn.setOnClickListener {

            //Nickname Validation
            //val validatedNinckname = nicknameValidation()

            //Gender Validation

            //Birth Validation
            val validatedBirth = birthValidation()

            //Height & Weight Validation
            if (binding.registerInfoHeightEt.text.toString() != "") newUser.height =
                binding.registerInfoHeightEt.text.toString().toInt()
            if (binding.registerInfoWeightEt.text.toString() != "") newUser.weight =
                binding.registerInfoWeightEt.text.toString().toInt()

            //ok -> 목표 프래그먼트 데이터 전달
            if (validatedBirth) {
                Log.d("INFO-FRAGMENT", newUser.toString())
                (activity as RegisterActivity).changeNextFragment(newUser)
            }
        }


        //keyboardUp(balloon)
    }


    /*Tool*/
    fun dpToSp(dp: Float, context: Context): Int {
        return (dpToPx(dp, context) / context.resources.displayMetrics.scaledDensity).toInt()
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
            .toInt()
    }

    /*Button*/
    private fun checkBtnState() {
        /*var isBirthCorrect = false
        //셋 다 잘 입력(1)거나, 셋다 아예 입력x(0) 여야만 true 나머지는 false
        if((date[0] == 1 && date[1] == 1 && date[2] == 1 ) || (date[0] == 0 && date[1] == 0 && date[2] == 0)) isBirthCorrect = true*/

        if (isNicknameCorrect && isGenderCorrect) {
            //버튼 색 바뀌게
            binding.registerInfoActionBtn.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_register_action_btn
                )
            )
            binding.registerInfoActionBtn.isEnabled = true

        } else {
            binding.registerInfoActionBtn.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_register_action_btn_disabled
                )
            )
            binding.registerInfoActionBtn.isEnabled = false
        }
    }

    /*Nickname*/
    private fun nicknameEt() {
        val nicknameEt = binding.registerInfoNicknameEt
        nicknameEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Focus
                nicknameEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
                nicknameTextWatcher()
            } else {
                if (nicknameEt.text.isEmpty()) {
                    nicknameEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))
                    isNicknameCorrect = false
                }

            }
        }
    }

    private fun nicknameTextWatcher() {
        val nicknameEt = binding.registerInfoNicknameEt
        nicknameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {

                if (nicknameEt.text.length > 8) {
                    //if(nicknameEt.text.length == 9){
                    //nicknameEt.startAnimation(animation)
                    //nicknameEt.requestFocus()
                    nicknameEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.secondary))
                    binding.registerInfoNicknameErrorTv.visibility = View.VISIBLE
                    isNicknameCorrect = false

                } else {
                    if (nicknameEt.text.isNotEmpty()) {
                        //닉네임에 반영
                        newUser.nickname = nicknameEt.text.toString()
                        Log.d("WATCHER/NICKNAME", newUser.toString())
                        nicknameEt.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.primary))
                        binding.registerInfoNicknameErrorTv.visibility = View.INVISIBLE
                        isNicknameCorrect = true
                    }
                }
                checkBtnState()
            }

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
        })
    }


    /*Gender*/
    private fun genderInput() {
        binding.registerInfoGenderRadiogrpGrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.register_info_gender_female_btn -> newUser.gender = "female"
                R.id.register_info_gender_male_btn -> newUser.gender = "male"
                R.id.register_info_gender_none_btn -> newUser.gender = "null"
            }
            Log.d("WATCHER/GENDER", newUser.toString())
            isGenderCorrect = true
            checkBtnState()
        })
    }


    /*Birth*/
    private fun birthEt() {
        //year
        val yearEt = binding.registerInfoBirthYearEt
        //var year: Int? = null
        yearEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Focus
                yearEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
                binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.primary))
                yearTextWatcher()
            } else {
                //Focus 떼졌을 때
                if (yearEt.text.isEmpty()) {
                    yearEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))
                    binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.primary_light))
                    //date[0] = 0
                }

            }
        }

        //month
        val monthEt = binding.registerInfoBirthMonthEt
        //var month: Int? = null
        monthEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Focus
                monthEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
                binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.primary))
                monthTextWatcher()
            } else {
                // No Focus
                if (monthEt.text.isEmpty()) {
                    monthEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))
                    binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.primary_light))
                    //date[1] = 0
                }
            }
        }

        //day
        val dayEt = binding.registerInfoBirthDayEt
        //var day: Int? = null
        dayEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Focus
                dayEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
                binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.primary))
                dayTextWatcher()
            } else {
                // Focus X
                if (dayEt.text.isEmpty()) {
                    dayEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))
                    binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.primary_light))
                    //date[2] = 0
                }
            }
        }


    }

    private fun yearTextWatcher() {
        val yearEt = binding.registerInfoBirthYearEt
        yearEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                if (yearEt.text.isNotEmpty()) {
                    /* //안 비었는데? -> validation 1900~작년(2021)
                     val year = parseInt(yearEt.text.toString())
                     if (year in 1900 until THIS_YEAR) {
                         //잘 입력됨 -> year ON*/
                    yearEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primary))
                    binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.primary))
                    /*     date[0] = 1
                     } else {
                         //잘못 입력됨 -> year OFF
                         date[0] = 4
                         //떨고 & 색 바뀌고
                         yearEt.startAnimation(animation)
                         yearEt.backgroundTintList =
                             ColorStateList.valueOf(resources.getColor(R.color.secondary))
                         binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.secondary))
                     }

                     checkBtnState()*/
                }
            }

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun monthTextWatcher() {
        val monthEt = binding.registerInfoBirthMonthEt
        monthEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                if (monthEt.text.isNotEmpty()) {
/*
                    // No Focus & Not Empty -> month Validation: 1~12
                    val month = parseInt(monthEt.text.toString())
                    if (month in 1..12) {
                        // 통과*/
                    monthEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primary))
                    binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.primary))
                    /*      date[1] = 1
                      } else {
                          // 실패
                          date[1] = 4
                          monthEt.startAnimation(animation)
                          monthEt.backgroundTintList =
                              ColorStateList.valueOf(resources.getColor(R.color.secondary))
                          binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.secondary))
                      }
                      checkBtnState()*/
                }
            }

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun dayTextWatcher() {
        val dayEt = binding.registerInfoBirthDayEt
        dayEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                if (dayEt.text.isNotEmpty()) {
/*
                    val day = parseInt(dayEt.text.toString())
                    // Day validation: 1~31
                    if(day in 1 .. 31){*/
                    dayEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primary))
                    binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.primary))
                    /*     date[2] = 1
                     }else{
                         date[2] = 4
                         dayEt.startAnimation(animation)
                         dayEt.backgroundTintList =
                             ColorStateList.valueOf(resources.getColor(R.color.secondary))
                         binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.secondary))
                     }
                     checkBtnState()*/
                }
            }

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
        })
    }

    /*Height*/
    private fun heightEt() {
        val heightEt = binding.registerInfoHeightEt
        val heightUnit = binding.registerInfoHeightUnitTv
        heightEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Focus
                heightEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
                heightUnit.setTextColor(resources.getColor(R.color.primary))
                //heightTextWatcher()
            } else {
                if (heightEt.text.isEmpty()) {
                    heightEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))
                    binding.registerInfoHeightUnitTv.setTextColor(resources.getColor(R.color.primary_light))
                }
            }
        }
    }

    private fun heightTextWatcher() {
        val heightEt = binding.registerInfoHeightEt
        heightEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                newUser.height = heightEt.text.toString().toInt()
                Log.d("WATCHER/HEIGHT", newUser.toString())

            }

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
        })
    }

    /*Weight*/
    private fun weightEt() {
        val weightEt = binding.registerInfoWeightEt
        val weightUnit = binding.registerInfoWeightUnitTv
        weightEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Focus
                weightEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
                weightUnit.setTextColor(resources.getColor(R.color.primary))
                //weightTextWatcher()
            } else {
                if (weightEt.text.isEmpty()) {
                    weightEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))
                    binding.registerInfoWeightUnitTv.setTextColor(resources.getColor(R.color.primary_light))

                }
            }
        }
    }

    private fun weightTextWatcher() {
        val weightEt = binding.registerInfoWeightEt
        weightEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                newUser.weight = weightEt.text.toString().toInt()
                Log.d("WATCHER/WEIGHT", newUser.toString())

            }

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
        })
    }


    /*Validation- Nickname, Birth*/
    private fun nicknameValidation(): Boolean {
        val nicknameEt = binding.registerInfoNicknameEt
        if (nicknameEt.text.length > 8) {
            nicknameEt.startAnimation(animation)
            nicknameEt.requestFocus()
            nicknameEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.secondary))
            binding.registerInfoNicknameErrorTv.visibility = View.VISIBLE
            return false
        } else {
            newUser.nickname = nicknameEt.text.toString()
            binding.registerInfoNicknameErrorTv.visibility = View.GONE
            nicknameEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.primary))
            return true
        }
    }

    private fun birthValidation(): Boolean {
        val yearEt = binding.registerInfoBirthYearEt
        var year: Int? = null
        var results = arrayOf(0, 0, 0)
        if (yearEt.text.toString() != "") {
            year = parseInt(yearEt.text.toString())
            if (year !in 1900 until THIS_YEAR) {
                yearEt.startAnimation(animation)
                yearEt.requestFocus()
                yearEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.secondary))
                binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.secondary))
                results[0] = 4
            } else results[0] = 1
        }

        val monthEt = binding.registerInfoBirthMonthEt
        var month: Int? = null
        if (monthEt.text.toString() != "") {
            month = parseInt(monthEt.text.toString())
            if (month !in 1..12) {
                monthEt.startAnimation(animation)
                monthEt.requestFocus()
                monthEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.secondary))
                binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.secondary))
                results[1] = 4
            } else results[1] = 1
        }

        val dayEt = binding.registerInfoBirthDayEt
        var day: Int? = null
        if (dayEt.text.toString() != "") {
            day = parseInt(dayEt.text.toString())
            if (day !in 1..31) {
                dayEt.startAnimation(animation)
                dayEt.requestFocus()
                dayEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.secondary))
                binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.secondary))
                results[2] = 4
            } else results[2] = 1
        }


        if (results[0] == 1 && results[1] == 1 && results[2] == 1) {
            newUser.birth = String.format("%04d.%02d.%02d", year, month, day)

            yearEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.primary))
            monthEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.primary))
            dayEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.primary))
            return true
        } else if (results[0] == 0 && results[1] == 0 && results[2] == 0) {
            newUser.birth = null
            return true
        }

        //하나라도 입력이 안됨!
        if (year == null) {
            yearEt.startAnimation(animation)
            yearEt.requestFocus()
            yearEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.secondary))
            binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.secondary))
        }
        if (month == null) {
            monthEt.startAnimation(animation)
            monthEt.requestFocus()
            monthEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.secondary))
            binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.secondary))
        }
        if (day == null) {
            dayEt.startAnimation(animation)
            monthEt.requestFocus()
            dayEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.secondary))
            binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.secondary))
        }
        return false
    }

    /*Tooltip*/
    private fun setHelpBallon() {
        val textSizeinSp = dpToSp(12F, requireContext()).toFloat()
        val balloon = Balloon.Builder(requireContext())
            .setWidth(200)
            .setHeight(60)
            .setText("칼로리 계산에 이용돼요! \n (미입력시 부정확할 수 있어요)")
            .setTextColorResource(R.color.white)
            .setTextSize(textSizeinSp)
            .setTextTypeface(R.font.namusquareround)
            .setArrowVisible(true)
            .setArrowSize(10)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setArrowPosition(0.2f)
            .setCornerRadius(40F)
            .setBackgroundColorResource(R.color.black_80)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setAutoDismissDuration(3000)
            .build()


        val xoff = Math.floor(200 / 2 + 200 * 0.3).toInt()
        binding.registerInfoWeightHelpIv.setOnClickListener {
            binding.registerInfoWeightHelpIv.showAlignTop(balloon, xoff, -5)
        }

        balloon.onBalloonClickListener = object : OnBalloonClickListener {
            override fun onBalloonClick(view: View) {
                balloon.dismiss()
            }
        }
    }

    /*Soft Keyboard*/
    private fun keyboardUp(balloon: Balloon) {
        //requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().getWindow(),
            onShowKeyboard = { keyboardHeight ->
                binding.registerInfoScrollLayout.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }

                Log.d("KEYBOARD", "Height = ${keyboardHeight}")
            })
    }


}


