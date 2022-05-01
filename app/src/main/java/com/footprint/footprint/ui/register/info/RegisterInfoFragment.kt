package com.footprint.footprint.ui.register.info

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RadioGroup
import androidx.core.view.updateLayoutParams
import com.footprint.footprint.R
import com.footprint.footprint.domain.model.InitUserModel
import com.footprint.footprint.databinding.FragmentRegisterInfoBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.register.RegisterActivity
import com.footprint.footprint.utils.KeyboardVisibilityUtils
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.convertDpToSp
import com.footprint.footprint.utils.setHeight
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.showAlignTop
import java.lang.Integer.parseInt
import java.time.LocalDate
import java.time.ZoneId

class RegisterInfoFragment() :
    BaseFragment<FragmentRegisterInfoBinding>(FragmentRegisterInfoBinding::inflate) {
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var animation: Animation
    private lateinit var rgPositionListener : ViewTreeObserver.OnGlobalLayoutListener

    private var scrollState: String = "DOWN" //UP, DOWN
    private var newUser: InitUserModel = InitUserModel()
    private var isNicknameCorrect = false
    private var isGenderCorrect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rgPositionListener = ViewTreeObserver.OnGlobalLayoutListener {
            val extraWidth = binding.registerInfoGenderRadiogrpGrp.measuredWidth - (binding.registerInfoGenderFemaleBtn.measuredWidth + binding.registerInfoGenderMaleBtn.measuredWidth + binding.registerInfoGenderNoneBtn.measuredWidth)
            binding.registerInfoGenderMaleBtn.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(extraWidth!! / 2, 0, extraWidth!! / 2, 0)
            }

            requireView().viewTreeObserver.removeOnGlobalLayoutListener(rgPositionListener)
        }
    }

    override fun initAfterBinding() {

        /*각 EditText에 입력 받기 */
        nicknameEt()
        genderInput()
        birthEt()
        weightEt()
        heightEt()

        /*툴팁*/
        setHelpBallon()

        /*스크롤뷰 초기화*/
        initScrollView()

        /*버튼 활성화 & 눌렀을 때*/
        binding.registerInfoActionBtn.setOnClickListener {
            //포커스 제거
            clearFocus()

            //Birth Validation
            val validatedBirth = birthValidation()

            //Height & Weight
            val validateHeight = heightValidation()
            val validateWeight = weightValidation()

            //ok -> 목표 프래그먼트 데이터 전달
            if (validatedBirth && validateHeight && validateWeight) {
                LogUtils.d("REGISTER-INFO/USER", newUser.toString())
                (activity as RegisterActivity).changeNextFragment(newUser)
            }
        }

        requireView().viewTreeObserver.addOnGlobalLayoutListener(rgPositionListener)
    }

    //스크롤뷰 초기화 함수
    private fun initScrollView() {
        //스크롤뷰 height 정해지면 -> 받아오기
        val sv = binding.registerInfoScrollviewV
        sv.getViewTreeObserver().addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                sv.getViewTreeObserver().removeOnGlobalLayoutListener(this)

                //내부 layout들 초기화
                binding.registerInfoNicknameAndGenderLayout.setHeight((sv.getHeight()) / 2)
                binding.registerInfoBirthAndWheightLayout.setHeight((sv.getHeight()) / 2)
                binding.registerInfoBlankV.setHeight((sv.getHeight()) / 2)

                //사용자 스크롤 막기
                sv.setScrollingEnabled(false)
            }
        })
    }


    //화면에 있는 모든 EditText 의 Focus 를 해제하는 함수
    private fun clearFocus() {
        binding.registerInfoNicknameEt.clearFocus()
        binding.registerInfoBirthYearEt.clearFocus()
        binding.registerInfoBirthMonthEt.clearFocus()
        binding.registerInfoBirthDayEt.clearFocus()
        binding.registerInfoHeightEt.clearFocus()
        binding.registerInfoWeightEt.clearFocus()
    }

    /*Button*/
    private fun checkBtnState() {
        // 닉네임 OK & 성별 OK => 버튼 활성화
        binding.registerInfoActionBtn.isEnabled = isNicknameCorrect && isGenderCorrect
    }

    /*Nickname: Focus, Watcher*/
    private fun nicknameEt() {
        val nicknameEt = binding.registerInfoNicknameEt
        nicknameEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) { // Focus
                nicknameTextWatcher()
            } else {
                if (nicknameEt.text.isEmpty()) {
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
                    if (nicknameEt.text.isNotEmpty()) { //닉네임에 반영
                        newUser.nickname = nicknameEt.text.toString()
                        LogUtils.d("REGISTER-INFO/NICKNAME-WATCHER", newUser.toString())
                        nicknameEt.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.primary))
                        binding.registerInfoNicknameErrorTv.visibility = View.GONE
                        isNicknameCorrect = true
                    } else //닉네임 입력창이 비어있을 때
                        isNicknameCorrect = false
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
            LogUtils.d("REGISTER-INFO/GENDER", newUser.toString())
            isGenderCorrect = true
            checkBtnState()
        })

    }


    /*Birth: Focus*/
    private fun birthEt() {
        //year
        val yearEt = binding.registerInfoBirthYearEt
        yearEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) { // Focus
                binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.primary))
                if(scrollState == "DOWN"){
                    scrollUp(isUp = true)
                }
            } else { // Focus X
                if (yearEt.text.isEmpty()) {
                    binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.primary_light))
                } else {
                    yearEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primary))
                    binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.primary))
                }

            }
        }

        //month
        val monthEt = binding.registerInfoBirthMonthEt
        monthEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) { // Focus
                if(scrollState == "DOWN"){
                    scrollUp(isUp = true)
                }
                binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.primary))
            } else { // Focus X
                if (monthEt.text.isEmpty()) {
                    binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.primary_light))
                } else {
                    monthEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primary))
                    binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.primary))
                }
            }
        }

        //day
        val dayEt = binding.registerInfoBirthDayEt
        dayEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) { // Focus
                if(scrollState == "DOWN"){
                    scrollUp(isUp = true)
                }
                binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.primary))
            } else { // Focus X
                if (dayEt.text.isEmpty()) {
                    binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.primary_light))
                } else {
                    dayEt.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primary))
                    binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.primary))
                }
            }
        }


    }


    /*Validation- Birth*/
    private fun birthValidation(): Boolean {
        animation = AnimationUtils.loadAnimation(activity, R.anim.shake)
        val nowDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val thisYear = nowDate.year
        val results = arrayOf(0, 0, 0)

        val yearEt = binding.registerInfoBirthYearEt
        var year: Int? = null
        if (yearEt.text.isNotEmpty()) {
            year = parseInt(yearEt.text.toString())
            if (year !in 1901 until thisYear) {
                yearEt.startAnimation(animation)
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
                dayEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.secondary))
                binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.secondary))
                results[2] = 4
            } else results[2] = 1
        }


        if (results[0] == 1 && results[1] == 1 && results[2] == 1) {
            newUser.birth = String.format("%04d-%02d-%02d", year, month, day)

            yearEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.primary))
            monthEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.primary))
            dayEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.primary))
            return true
        } else if (results[0] == 0 && results[1] == 0 && results[2] == 0) {
            newUser.birth = "1900-01-01"
            return true
        }

        //하나라도 입력이 안됨!
        if (year == null) {
            yearEt.startAnimation(animation)
            yearEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.secondary))
            binding.registerInfoBirthYearUnitTv.setTextColor(resources.getColor(R.color.secondary))
        }
        if (month == null) {
            monthEt.startAnimation(animation)
            monthEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.secondary))
            binding.registerInfoBirthMonthUnitTv.setTextColor(resources.getColor(R.color.secondary))
        }
        if (day == null) {
            dayEt.startAnimation(animation)
            dayEt.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.secondary))
            binding.registerInfoBirthDayUnitTv.setTextColor(resources.getColor(R.color.secondary))
        }
        return false
    }


    /*Height: Focus*/
    private fun heightEt() {
        val heightEt = binding.registerInfoHeightEt
        val heightUnit = binding.registerInfoHeightUnitTv
        heightEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) { // Focus
                if(scrollState == "DOWN"){
                    scrollUp(isUp = true)
                }
                heightUnit.setTextColor(resources.getColor(R.color.primary))
            } else {
                if (heightEt.text.isEmpty()) {
                    binding.registerInfoHeightUnitTv.setTextColor(resources.getColor(R.color.primary_light))
                }
            }
        }
    }

    private fun heightValidation(): Boolean {
        if (binding.registerInfoHeightEt.text.isNotEmpty()) { //값 O
            val height = binding.registerInfoHeightEt.text.toString().toInt()
            if (height !in 100..250) {
                binding.registerInfoHeightEt.startAnimation(animation)
                binding.registerInfoHeightEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.secondary))
                binding.registerInfoHeightUnitTv.setTextColor(resources.getColor(R.color.secondary))
                return false
            } else {
                newUser.height = height
                return true
            }
        } else { //값 X
            newUser.height = 0
            return true
        }
    }

    /*Weight: Focus*/
    private fun weightEt() {
        val weightEt = binding.registerInfoWeightEt
        val weightUnit = binding.registerInfoWeightUnitTv
        weightEt.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) { // Focus
                if(scrollState == "DOWN"){
                    scrollUp(isUp = true)
                }
                weightUnit.setTextColor(resources.getColor(R.color.primary))
            } else {
                if (weightEt.text.isEmpty()) {
                    binding.registerInfoWeightUnitTv.setTextColor(resources.getColor(R.color.primary_light))

                }
            }
        }
    }

    private fun weightValidation(): Boolean {
        if (binding.registerInfoWeightEt.text.isNotEmpty()) {
            val weight = binding.registerInfoWeightEt.text.toString().toInt()
            if (weight !in 30..200) {
                binding.registerInfoWeightEt.startAnimation(animation)
                binding.registerInfoWeightEt.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.secondary))
                binding.registerInfoWeightUnitTv.setTextColor(resources.getColor(R.color.secondary))
                return false
            } else {
                newUser.weight = weight
                return true
            }
        } else {
            newUser.weight = 0
            return true
        }
    }

    /*Tooltip*/
    private fun setHelpBallon() {
        val textSizeinSp = convertDpToSp(requireContext(), 12).toFloat()
        val balloon = Balloon.Builder(requireContext())
            .setWidth(200)
            .setHeight(60)
            .setText("칼로리 계산에 이용돼요! \n (미입력시 부정확할 수 있어요)")
            .setTextColorResource(R.color.white)
            .setTextSize(textSizeinSp)
            .setTextTypeface(R.font.namusquareround)
            .setIsVisibleArrow(true)
            .setArrowSize(10)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setArrowColorMatchBalloon(true)
            .setArrowPosition(0.2f)
            .setCornerRadius(40F)
            .setBackgroundColor(Color.parseColor("#CC4D4D4F"))
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setDismissWhenClicked(true)
            .setAutoDismissDuration(3000)
            .setFocusable(false)
            .build()

        //width = 200/2 중간 => 에서 0.3만큼 오른쪽으로 치우치게
        val xoff = Math.floor(200 / 2 + 200 * 0.3).toInt()

        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().getWindow(),
            onHideKeyboard = {
                if(scrollState == "UP")
                    scrollUp(false)
            }
        )

        //키보드 올라갔을 때만 툴팁 띄우기 (임시)
        binding.registerInfoWeightHelpIv.setOnClickListener {
                binding.registerInfoWeightHelpIv.showAlignTop(balloon, xoff, -5)
        }
    }

    //자동 스크롤 함수
    private fun scrollUp(isUp: Boolean) {
        if (isUp) {
            binding.registerInfoScrollviewV.setScrollingEnabled(true)
            binding.registerInfoScrollviewV.smoothScrollTo(
                0,
                binding.registerInfoNicknameAndGenderLayout.height - 50,
                500
            )
            binding.registerInfoScrollviewV.setScrollingEnabled(false)
            scrollState = "UP"
        } else {
            binding.registerInfoScrollviewV.setScrollingEnabled(true)
            binding.registerInfoScrollviewV.smoothScrollTo(0, 0, 500)
            binding.registerInfoScrollviewV.setScrollingEnabled(false)
            scrollState = "DOWN"
        }
    }

}