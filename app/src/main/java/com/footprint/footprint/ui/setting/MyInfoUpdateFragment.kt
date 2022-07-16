package com.footprint.footprint.ui.setting

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.navigation.fragment.navArgs
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentMyInfoUpdateBinding
import com.footprint.footprint.domain.model.MyInfoUserModel
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.error.ErrorActivity
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.convertDpToSp
import com.footprint.footprint.utils.getJwt
import com.footprint.footprint.viewmodel.MyInfoViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.skydoves.balloon.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.math.floor

class MyInfoUpdateFragment :
    BaseFragment<FragmentMyInfoUpdateBinding>(FragmentMyInfoUpdateBinding::inflate){

    private val myInfoVm: MyInfoViewModel by sharedViewModel()
    private lateinit var networkErrSb: Snackbar

    private lateinit var user: MyInfoUserModel

    private val args: MyInfoUpdateFragmentArgs by navArgs()
    private lateinit var animation: Animation   //EditText 애니메이션
    private lateinit var rgPositionListener: ViewTreeObserver.OnGlobalLayoutListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = Gson().fromJson(args.user, MyInfoUserModel::class.java)    //MyInfoFragment 로부터 user 정보 전달받기

        rgPositionListener = ViewTreeObserver.OnGlobalLayoutListener() {
            val extraWidth = binding.myInfoUpdateGenderRg.measuredWidth - (binding.myInfoUpdateGenderFemaleRb.measuredWidth + binding.myInfoUpdateGenderMaleRb.measuredWidth + binding.myInfoUpdateGenderNoneRb.measuredWidth)
            binding.myInfoUpdateGenderMaleRb.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(extraWidth!! / 2, 0, extraWidth!! / 2, 0)
            }

            requireView().viewTreeObserver.removeOnGlobalLayoutListener(rgPositionListener)
        }
    }

    override fun initAfterBinding() {
        if (!::animation.isInitialized)
            animation =
                AnimationUtils.loadAnimation(requireActivity(), R.anim.shake)   //EditText 애니메이션 초기화

        setUpdateUI(user)
        setMyEventListener()
        setHelpBalloon()    //툴팁

        requireView().viewTreeObserver.addOnGlobalLayoutListener(rgPositionListener)

        observe()
    }

    //내 정보 "수정" 화면
    private fun setUpdateUI(user: MyInfoUserModel) {
        binding.myInfoUpdateNicknameEt.setText(user.nickname)   //닉네임

        binding.myInfoUpdateGenderRg.apply {    //성별
            when (user.gender) {
                "female" -> check(R.id.my_info_update_gender_female_rb)
                "male" -> check(R.id.my_info_update_gender_male_rb)
                else -> check(R.id.my_info_update_gender_none_rb)
            }
        }

        if (user.birth != "1900-01-01") {    //생년월일
            val birthList = user.birth.split("-")
            binding.myInfoUpdateBirthYearEt.setText(birthList[0])
            binding.myInfoUpdateBirthMonthEt.setText(birthList[1])
            binding.myInfoUpdateBirthDayEt.setText(birthList[2])
        }

        if (user.height != 0)  //키
            binding.myInfoUpdateHeightEt.setText(user.height.toString())

        if (user.weight != 0)  //몸무게
            binding.myInfoUpdateWeightEt.setText(user.weight.toString())
    }

    private fun setMyEventListener() {
        //뒤로가기 아이콘 클릭 리스너 -> 뒤로가기
        binding.myInfoUpdateBackIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //닉네임 글자수가 8글자를 넘어가는지 체크하는 이벤트 리스너
        binding.myInfoUpdateNicknameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.length > 8) {  //닉네임 글자수가 8글자를 넘어갈 때
                    binding.myInfoUpdateNicknameEt.isSelected = true
                    binding.myInfoUpdateNicknameErrorTv.visibility = View.VISIBLE
                } else {    //닉네임 글자수가 8글자를 넘어가지 않을 때
                    binding.myInfoUpdateNicknameEt.isSelected = false
                    binding.myInfoUpdateNicknameErrorTv.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        //저장 텍스트뷰 클릭 리스너
        binding.myInfoUpdateSaveTv.setOnClickListener {
            clearFocus()    //EditText 에 있던 모든 포커스 해제
            hideKeyboard()  //키보드 내리기

            if (validate()) {   //유효성 검사를 통과한 경우 -> 1. 사용자 정보 수정 API 를 요청한다. 2. 요청 성공: MyInfoUpdateFragment 화면으로 돌아간다.
                myInfoVm.updateUser(bindUser())
            }
        }
    }

    //화면에 있는 모든 EditText 의 Focus 를 해제하는 함수
    private fun clearFocus() {
        binding.myInfoUpdateNicknameEt.clearFocus()
        binding.myInfoUpdateBirthYearEt.clearFocus()
        binding.myInfoUpdateBirthMonthEt.clearFocus()
        binding.myInfoUpdateBirthDayEt.clearFocus()
        binding.myInfoUpdateHeightEt.clearFocus()
        binding.myInfoUpdateWeightEt.clearFocus()
    }

    //사용자가 입력한 정보들에 대한 유효성 검사 함수
    private fun validate(): Boolean {
        var result = true   //유효성 검사 결과

        //단위 텍스트뷰 색 초기화
        binding.myInfoUpdateBirthYearUnitTv.setTextColor(Color.parseColor("#4FB8E7"))
        binding.myInfoBirthUpdateMonthUnitTv.setTextColor(Color.parseColor("#4FB8E7"))
        binding.myInfoUpdateBirthDayUnitTv.setTextColor(Color.parseColor("#4FB8E7"))
        binding.myInfoUpdateHeightUnitTv.setTextColor(Color.parseColor("#4FB8E7"))
        binding.myInfoUpdateWeightUnitTv.setTextColor(Color.parseColor("#4FB8E7"))

        //닉네임
        if (binding.myInfoUpdateNicknameEt.text.isBlank() || binding.myInfoUpdateNicknameEt.isSelected) {  //닉네임(필수값)이 비어 있거나 8자 글자 이상 입력한 상태(isSelected==true)일 경우
            binding.myInfoUpdateNicknameEt.isSelected = true
            binding.myInfoUpdateNicknameEt.startAnimation(animation)

            result = false
        } else  //닉네임 유효성 검사 통과 -> 닉네임(필수값)이 비어 있지 않고 8자 이내로 입력한 경우
            binding.myInfoUpdateNicknameEt.isSelected = false

        //생년월일
        if (binding.myInfoUpdateBirthYearEt.text.isNotBlank() || binding.myInfoUpdateBirthMonthEt.text.isNotBlank() || binding.myInfoUpdateBirthDayEt.text.isNotBlank()) {    //생년월일 중 하나라도 입력돼 있으면 생년월일에 대한 유효성 검사 시작

            if (binding.myInfoUpdateBirthYearEt.text.isBlank() || binding.myInfoUpdateBirthYearEt.text.toString()
                    .toInt() !in 1900 until 2022
            ) {    //년도 데이터가 비어 있거나 1900~2022 년도 안에 있는 숫자가 아닐 경우
                binding.myInfoUpdateBirthYearEt.startAnimation(animation)
                binding.myInfoUpdateBirthYearUnitTv.setTextColor(Color.parseColor("#FFC01D"))
                binding.myInfoUpdateBirthYearEt.isSelected = true

                result = false
            } else  //유효성 검사를 통과한 년도 데이터일 경우
                binding.myInfoUpdateBirthYearEt.isSelected = false

            if (binding.myInfoUpdateBirthMonthEt.text.isBlank() || binding.myInfoUpdateBirthMonthEt.text.toString()
                    .toInt() !in 1..12
            ) {    //월 데이터가 비어 있거나 1~12 사이에 있는 숫자가 아닐 경우
                binding.myInfoUpdateBirthMonthEt.startAnimation(animation)
                binding.myInfoBirthUpdateMonthUnitTv.setTextColor(Color.parseColor("#FFC01D"))
                binding.myInfoUpdateBirthMonthEt.isSelected = true

                result = false
            } else  //유효성 검사를 통과한 월 데이터일 경우
                binding.myInfoUpdateBirthMonthEt.isSelected = false

            if (binding.myInfoUpdateBirthDayEt.text.isBlank()) {    //일 데이터가 비어 있을 경우
                binding.myInfoUpdateBirthDayEt.startAnimation(animation)
                binding.myInfoUpdateBirthDayUnitTv.setTextColor(Color.parseColor("#FFC01D"))
                binding.myInfoUpdateBirthDayEt.isSelected = true

                result = false
            } else {    //일 데이터가 비어 있지 않을 경우 -> 각 월마다 유효성 검사 시작
                when (binding.myInfoUpdateBirthMonthEt.text.toString()) {
                    "4", "6", "9", "11" -> {    //30일까지 있는 월에서 30일을 넘어간 경우
                        if (binding.myInfoUpdateBirthDayEt.text.toString().toInt() > 30) {
                            binding.myInfoUpdateBirthDayEt.startAnimation(animation)
                            binding.myInfoUpdateBirthDayUnitTv.setTextColor(Color.parseColor("#FFC01D"))
                            binding.myInfoUpdateBirthDayEt.isSelected = true

                            result = false
                        } else  //유효성 검사를 통과한 일 데이터(30일까지 있는 월에 대한)일 경우
                            binding.myInfoUpdateBirthDayEt.isSelected = false
                    }

                    "2" -> {    //2월 달의 경우 -> 윤달 이런거 생각 안하고 29 를 초과한 경우로만 생각했음.
                        if (binding.myInfoUpdateBirthDayEt.text.toString()
                                .toInt() > 29
                        ) {    //29일을 초과한 데이터가 입력된 경우
                            binding.myInfoUpdateBirthDayEt.startAnimation(animation)
                            binding.myInfoUpdateBirthDayUnitTv.setTextColor(Color.parseColor("#FFC01D"))
                            binding.myInfoUpdateBirthDayEt.isSelected = true

                            result = false
                        } else  //유효성 검사를 통과한 일 데이터(2월일 때)일 경우
                            binding.myInfoUpdateBirthDayEt.isSelected = false
                    }

                    else -> {   //그외 -> 1. 31일까지 있는 월일 경우   2. 아직 월 데이터가 입력되지 않은 경우
                        if (binding.myInfoUpdateBirthDayEt.text.toString()
                                .toInt() > 31
                        ) {    //31일을 초과한 데이터가 입력된 경우
                            binding.myInfoUpdateBirthDayEt.startAnimation(animation)
                            binding.myInfoUpdateBirthDayUnitTv.setTextColor(Color.parseColor("#FFC01D"))
                            binding.myInfoUpdateBirthDayEt.isSelected = true

                            result = false
                        } else  //유효성 검사를 통과한 일 데이터(31일까지 있을 때)일 경우
                            binding.myInfoUpdateBirthDayEt.isSelected = false
                    }
                }
            }
        } else {    //사용자가 생년월일을 입력하지 않은 경우 -> 생년월일은 필수 데이터가 아니므로 유효성 검사 통과
            binding.myInfoUpdateBirthYearEt.isSelected = false
            binding.myInfoUpdateBirthMonthEt.isSelected = false
            binding.myInfoUpdateBirthDayEt.isSelected = false
        }

        //키
        if (binding.myInfoUpdateHeightEt.text.isNotBlank() && binding.myInfoUpdateHeightEt.text.toString()
                .toInt() !in 100..250
        ) {  //키 데이터가 입력돼 있고, 100~250 사이 숫자가 아니면
            binding.myInfoUpdateHeightEt.startAnimation(animation)
            binding.myInfoUpdateHeightUnitTv.setTextColor(Color.parseColor("#FFC01D"))
            binding.myInfoUpdateHeightEt.isSelected = true

            result = false
        } else  //유효성 검사를 통과한 키 데이터의 경우
            binding.myInfoUpdateHeightEt.isSelected = false

        //몸무게
        if (binding.myInfoUpdateWeightEt.text.isNotBlank() && binding.myInfoUpdateWeightEt.text.toString()
                .toInt() !in 30..200
        ) {   //몸무게 데이터가 입력돼 있고, 30~200 사이 숫자가 아니면
            binding.myInfoUpdateWeightEt.startAnimation(animation)
            binding.myInfoUpdateWeightUnitTv.setTextColor(Color.parseColor("#FFC01D"))
            binding.myInfoUpdateWeightEt.isSelected = true

            result = false
        } else  //몸무게 데이터 유효성 검사 통과
            binding.myInfoUpdateWeightEt.isSelected = false

        return result   //유효성 검사 결과 반환
    }

    //사용자 정보 수정 후 사용자 정보 조회 화면에 넘겨줄 유저 데이터 바인딩 함수
    private fun bindUser(): MyInfoUserModel {
        val user = MyInfoUserModel()

        user.nickname = binding.myInfoUpdateNicknameEt.text.toString()    //닉네임(필수)

        user.gender = when (binding.myInfoUpdateGenderRg.checkedRadioButtonId) {  //성별(필수)
            R.id.my_info_update_gender_female_rb -> "female"
            R.id.my_info_update_gender_male_rb -> "male"
            else -> "null"
        }

        if (binding.myInfoUpdateBirthYearEt.text.isNotBlank())    //생년월일
            user.birth = String.format("%04d-%02d-%02d",
                binding.myInfoUpdateBirthYearEt.text.toString().toInt(), binding.myInfoUpdateBirthMonthEt.text.toString().toInt(), binding.myInfoUpdateBirthDayEt.text.toString().toInt())
        else
            user.birth = "1900-01-01"

        if (binding.myInfoUpdateHeightEt.text.isNotBlank())   //키
            user.height = binding.myInfoUpdateHeightEt.text.toString().toInt()

        if (binding.myInfoUpdateWeightEt.text.isNotBlank())   //체중
            user.weight = binding.myInfoUpdateWeightEt.text.toString().toInt()

        return user
    }

    private fun setHelpBalloon() {
        val textSize = convertDpToSp(requireContext(), 12).toFloat()
        val balloon = Balloon.Builder(requireContext())
            .setWidth(200)
            .setHeight(60)
            .setText("칼로리 계산에 이용돼요! \n (미입력시 부정확할 수 있어요)")
            .setTextColorResource(R.color.white)
            .setTextSize(textSize)
            .setTextTypeface(R.font.namusquareround)
            .setIsVisibleArrow(true)
            .setArrowSize(10)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setArrowPosition(0.2f)
            .setCornerRadius(40F)
            .setBackgroundColorResource(R.color.black_80)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setAutoDismissDuration(3000)
            .build()

        //Help 버튼 누르면 => 해당 위치에 뜸
        val xOff = floor(200 / 2 + 200 * 0.3).toInt()
        binding.myInfoUpdateWeightHelpIv.setOnClickListener {
            binding.myInfoUpdateWeightHelpIv.showAlignTop(balloon, xOff, -5)
        }

        //말풍선 클릭 시 => 사라짐
        balloon.setOnBalloonClickListener(OnBalloonClickListener {
            balloon.dismiss()
        })
    }

    private fun observe() {
        myInfoVm.mutableErrorType.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                ErrorType.NETWORK ->{
                    networkErrSb = Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) {
                        if (validate())
                            myInfoVm.updateUser(bindUser())
                    }

                    networkErrSb.show()
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    startErrorActivity("MyInfoUpdateFragment")
                }
            }
        })

        myInfoVm.isUpdate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
               (requireActivity()).onBackPressed()
        })
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }
}