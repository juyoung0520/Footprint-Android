package com.footprint.footprint.ui.register.info

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentRegisterInfoBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.KeyboardVisibilityUtils
import android.view.View.OnFocusChangeListener
import com.footprint.footprint.data.model.User
import com.skydoves.balloon.*
import java.lang.Integer.parseInt
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import com.skydoves.balloon.OnBalloonClickListener
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.footprint.footprint.ui.register.RegisterActivity
import com.footprint.footprint.utils.getLoginStatus
import com.footprint.footprint.utils.getToken
import java.time.LocalDate
import java.time.ZoneId


class RegisterInfoFragment() :
    BaseFragment<FragmentRegisterInfoBinding>(FragmentRegisterInfoBinding::inflate) {
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    private var newUser: User = User()
    private var isNicknameCorrect = false
    private var isGenderCorrect = false

    private lateinit var animation: Animation

    override fun initAfterBinding() {
        /*각 EditText에 입력 받기 */
        nicknameEt()
        genderInput()
        birthEt()
        weightEt()
        heightEt()

        setHelpBallon()

        /*버튼 활성화 & 눌렀을 때*/
        binding.registerInfoActionBtn.setOnClickListener {

            //Nickname Validation => O
            //Gender Validation   => O

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


        //keyboardUp()
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

    /*Nickname: Focus, Watcher*/
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
                        binding.registerInfoNicknameErrorTv.visibility = View.GONE
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
                }

            }
        }

        //month
        val monthEt = binding.registerInfoBirthMonthEt
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
                }
            }
        }

        //day
        val dayEt = binding.registerInfoBirthDayEt
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
                }
            }
        }


    }

    private fun yearTextWatcher() {
        val yearEt = binding.registerInfoBirthYearEt
        yearEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                if (yearEt.text.isNotEmpty()) {
                    yearEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primary))
                    binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.primary))
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
                    monthEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primary))
                    binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.primary))
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
                    dayEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primary))
                    binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.primary))
                }
            }

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
        })
    }

    /*Validation- Birth*/
    private fun birthValidation(): Boolean {
        animation = AnimationUtils.loadAnimation(activity, R.anim.shake)
        val nowDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val THIS_YEAR = nowDate.year

        val yearEt = binding.registerInfoBirthYearEt
        var year: Int? = null
        var results = arrayOf(0, 0, 0)
        if (yearEt.text.isNotEmpty()) {
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
        if (monthEt.text.isNotEmpty()) {
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
        if (dayEt.text.isNotEmpty()) {
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
            } else {
                if (heightEt.text.isEmpty()) {
                    heightEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white_underline))
                    binding.registerInfoHeightUnitTv.setTextColor(resources.getColor(R.color.primary_light))
                }
            }
        }
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
    private fun keyboardUp() {
//        val middle1H = binding.registerInfoMiddle1Layout.height
//        val middle2H = binding.registerInfoMiddle2Layout.height
/*
        val animUp = TranslateAnimation(0f, 0f, middle2H.toFloat(), middle1H.toFloat())
        val animDown = TranslateAnimation(0f, 0f, middle1H.toFloat(), middle2H.toFloat())*/

        //requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().getWindow(),
            onShowKeyboard = {
 /*               if (!binding.registerInfoNicknameEt.isFocused) {
                    val isYearFocused = binding.registerInfoBirthYearEt.isFocused
                    val isMonthFocused = binding.registerInfoBirthMonthEt.isFocused
                    val isDayFocused = binding.registerInfoBirthDayEt.isFocused
                    val isHeightFocused = binding.registerInfoHeightEt.isFocused
                    val isWeightFocused = binding.registerInfoWeightEt.isFocused
                    Log.d(
                        "FOCUSE",
                        "year: ${isYearFocused} month: ${isMonthFocused} day: ${isDayFocused}\n" + "height: ${isHeightFocused} weight: ${isWeightFocused}"
                    )

                    val activity = activity as RegisterActivity?
                    var view = activity!!.currentFocus
                    Log.d("FOCUS", "현재 focus는 ${view}")

                    if (isYearFocused) binding.registerInfoBirthYearEt.requestFocus()
                    if (isMonthFocused) binding.registerInfoBirthMonthEt.requestFocus()
                    if (isDayFocused) binding.registerInfoBirthDayEt.requestFocus()
                    if (isHeightFocused) binding.registerInfoHeightEt.requestFocus()
                    if (isWeightFocused) binding.registerInfoWeightEt.requestFocus()


                    animUp.duration = 2000
                    animUp.fillAfter = true
                    //binding.registerInfoMiddle2Layout.startAnimation(animUp)
                    binding.registerInfoMiddle1Layout.visibility = View.GONE
                    binding.registerInfoMiddle3Layout.visibility = View.VISIBLE
                    view = activity.currentFocus
                    Log.d("FOCUS", "다음 focus는 ${view}")

                }*/
            },
            onHideKeyboard = {
                /*animDown.duration = 2000
                animDown.fillAfter = true
                binding.registerInfoMiddle3Layout.visibility = View.GONE
                binding.registerInfoMiddle1Layout.visibility = View.VISIBLE
                binding.registerInfoMiddle2Layout.startAnimation(animDown)*/
            }
        )
    }

    /*회원가입 API*/
    private fun callSignUpAPI(){
        //1. 로그인 상태, 토큰
        val loginStatus = getLoginStatus(requireContext())
        val IdToken = getToken(requireContext())

        //2. 회원 정보(닉네임/성별/생년월일/키/몸무게), 목표 정보(목표 요일/목표 시간/목표 시간대)
        //newUser
        //3. 서버에 요청
    }

}


