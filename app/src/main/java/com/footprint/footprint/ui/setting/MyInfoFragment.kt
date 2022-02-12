package com.footprint.footprint.ui.setting

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.model.SimpleUserModel
import com.footprint.footprint.data.remote.achieve.AchieveService
import com.footprint.footprint.data.remote.user.User
import com.footprint.footprint.data.remote.user.UserService
import com.footprint.footprint.databinding.FragmentMyInfoBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.main.home.HomeView
import com.footprint.footprint.utils.convertDpToSp
import com.footprint.footprint.utils.isNetworkAvailable
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.skydoves.balloon.*
import kotlin.math.floor

class MyInfoFragment : BaseFragment<FragmentMyInfoBinding>(FragmentMyInfoBinding::inflate),
    HomeView {

    private lateinit var user: SimpleUserModel

    override fun initAfterBinding() {
    }

    override fun onStart() {
        super.onStart()

        //유저 정보 조회 API 호출
        UserService.getUser(this)
    }


    //내 정보 "조회" 화면
    private fun setLookUI(user: SimpleUserModel) {
        binding.myInfoNicknameEt.setText(user.nickname) //닉네임

        binding.myInfoGenderRg.apply {  //성별
            when (user.gender) {
                "female" -> check(R.id.my_info_gender_female_rb)
                "male" -> check(R.id.my_info_gender_male_rb)
                else -> check(R.id.my_info_gender_none_rb)
            }
        }

        setBirthUI(user.birth)  //생년월일

        if (user.height == 0) {  //키 데이터가 없을 때
            binding.myInfoHeightEt.setBackgroundResource(R.drawable.bg_my_info_et_white_dark)
            binding.myInfoHeightUnitTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white_dark
                )
            )
        } else {    //키 데이터가 있을 때
            binding.myInfoHeightEt.setBackgroundResource(R.drawable.bg_my_info_et_primary)
            binding.myInfoHeightUnitTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary
                )
            )
            binding.myInfoHeightEt.setText(user.height.toString())
        }

        if (user.weight == 0) {  //체중 데이터가 없을 때
            binding.myInfoWeightEt.setBackgroundResource(R.drawable.bg_my_info_et_white_dark)
            binding.myInfoWeightUnitTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white_dark
                )
            )
        } else {    //체중 데이터가 있을 때
            binding.myInfoWeightEt.setBackgroundResource(R.drawable.bg_my_info_et_primary)
            binding.myInfoWeightUnitTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary
                )
            )
            binding.myInfoWeightEt.setText(user.weight.toString())
        }
    }

    private fun setMyEventListener() {
        //뒤로가기 이미지뷰 클릭 리스너 -> 프래그먼트 종료
        binding.myInfoBackIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //수정 텍스트뷰 클릭 리스너 -> user 데이터를 전달하면서 MyInfoUpdateFragment 로 이동
        binding.myInfoUpdateTv.setOnClickListener {
            val action = MyInfoFragmentDirections.actionMyInfoFragmentToMyInfoUpdateFragment(
                Gson().toJson(user)
            )
            findNavController().navigate(action)
        }
    }

    private fun setBirthUI(birth: String) {
        if (birth == "1900-01-01") {  //생년월일 정보를 입력하지 않았을 때
            //년도
            binding.myInfoBirthYearEt.setBackgroundResource(R.drawable.bg_my_info_et_white_dark)
            binding.myInfoBirthYearUnitTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white_dark
                )
            )

            //월
            binding.myInfoBirthMonthEt.setBackgroundResource(R.drawable.bg_my_info_et_white_dark)
            binding.myInfoBirthMonthUnitTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white_dark
                )
            )

            //일
            binding.myInfoBirthDayEt.setBackgroundResource(R.drawable.bg_my_info_et_white_dark)
            binding.myInfoBirthDayUnitTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white_dark
                )
            )
        } else {    //생년월일 정보를 입력했을 때
            val birthList = birth.split("-")

            //년도
            binding.myInfoBirthYearEt.setText(birthList[0])
            binding.myInfoBirthYearEt.setBackgroundResource(R.drawable.bg_my_info_et_primary)
            binding.myInfoBirthYearUnitTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary
                )
            )

            //월
            binding.myInfoBirthMonthEt.setText(birthList[1])
            binding.myInfoBirthMonthEt.setBackgroundResource(R.drawable.bg_my_info_et_primary)
            binding.myInfoBirthMonthUnitTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary
                )
            )

            //일
            binding.myInfoBirthDayEt.setText(birthList[2])
            binding.myInfoBirthDayEt.setBackgroundResource(R.drawable.bg_my_info_et_primary)
            binding.myInfoBirthDayUnitTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary
                )
            )
        }
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
        binding.myInfoWeightHelpIv.setOnClickListener {
            binding.myInfoWeightHelpIv.showAlignTop(balloon, xOff, -5)
        }

        //말풍선 클릭 시 => 사라짐
        balloon.setOnBalloonClickListener(OnBalloonClickListener {
            balloon.dismiss()
        })
    }

    /*유저 정보 조회 API*/
    override fun onUserSuccess(user: User) {
        Log.d("MYINFO(USER)/API-SUCCESS", user.toString())

        this.user = SimpleUserModel(user.nickname, user.sex, user.birth, user.height, user.weight)

        binding.myInfoDayLoadingBgV.visibility = View.GONE
        binding.myInfoDayLoadingPb.visibility = View.GONE
        setLookUI(this.user) //내 정보 조회 화면 데이터 바인딩
        setMyEventListener()
        setHelpBalloon()    //툴팁

        Log.d("MYINFO(USER)", this.user.toString())
    }

    override fun onHomeFailure(code: Int, message: String) {
        Log.d("MYINFO(USER)/API-FAILURE", code.toString() + message)

        val text = if(!isNetworkAvailable(requireContext())){ //네트워크 에러
            getString(R.string.error_network)
        }else{ //나머지
            getString(R.string.error_api_fail)
        }
        Snackbar.make(requireView(), text, Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) {
            UserService.getUser(this)
        }.show()
    }
}