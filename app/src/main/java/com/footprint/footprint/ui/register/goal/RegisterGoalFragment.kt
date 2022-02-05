package com.footprint.footprint.ui.register.goal

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.footprint.footprint.R
import com.footprint.footprint.data.model.UserModel
import com.footprint.footprint.data.remote.infos.InfoService
import com.footprint.footprint.databinding.FragmentRegisterGoalBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.DayRVAdapter
import com.footprint.footprint.ui.dialog.WalkTimeDialogFragment
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.register.RegisterView
import com.footprint.footprint.utils.*

class RegisterGoalFragment() :
    BaseFragment<FragmentRegisterGoalBinding>(FragmentRegisterGoalBinding::inflate), RegisterView {
    private lateinit var dayRVAdapter: DayRVAdapter
    private lateinit var walkTimeDialogFragment: WalkTimeDialogFragment
    private lateinit var userModel: UserModel

    override fun initAfterBinding() {
        initAdapter()
        initWalkTimeDialog()
        setMyEventListener()
    }

    private fun initAdapter() {
        //목표 요일 어댑터 설정
        val deviceWidth = getDeviceWidth()
        dayRVAdapter = DayRVAdapter((deviceWidth - convertDpToPx(requireContext(), 88)) / 7)
        dayRVAdapter.setMyItemClickListener(object : DayRVAdapter.MyItemClickListener {
            override fun saveDay(day: Int) {
                userModel.goalDay.add(day)
                validate()
            }

            override fun removeDay(day: Int) {
                userModel.goalDay.remove(day)
                validate()
            }
        })

        binding.goalSettingGoalDayRv.adapter = dayRVAdapter
    }

    private fun initWalkTimeDialog() {
        walkTimeDialogFragment = WalkTimeDialogFragment()

        walkTimeDialogFragment.setMyDialogCallback(object :
            WalkTimeDialogFragment.MyDialogCallback {
            override fun complete(timeStr: String, minute: Int) {
                binding.goalSettingGoalWalkTimeBtn.text = timeStr
                binding.goalSettingGoalWalkTimeBtn.isSelected = true

                binding.goalSettingGoalWalkTimeRg.check(R.id.goal_setting_direct_setting_rb)

                userModel.goalWalkTime = minute

                validate()
            }
        })
    }

    private fun setMyEventListener() {
        //목표시각 버튼 클릭 리스너
        binding.goalSettingGoalWalkTimeBtn.setOnClickListener {
            if (binding.goalSettingGoalWalkTimeSv.visibility == View.GONE) {
                fadeIn(binding.goalSettingGoalWalkTimeSv)

                (it as Button).setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_up_arrow),
                    null
                )
            } else {
                fadeOut(binding.goalSettingGoalWalkTimeSv)

                (it as Button).setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                    null
                )
            }
        }

        //산책 시간대 버튼 클릭 리스너
        binding.goalSettingWalkSlotBtn.setOnClickListener {
            if (binding.goalSettingWalkSlotSv.visibility == View.GONE) {
                fadeIn(binding.goalSettingWalkSlotSv)

                (it as Button).setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_up_arrow),
                    null
                )
            } else {
                fadeOut(binding.goalSettingWalkSlotSv)

                (it as Button).setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                    null
                )
            }
        }

        //목표 산책 시간 라디오그룹 체크 리스너
        binding.goalSettingGoalWalkTimeRg.setOnCheckedChangeListener { radioGroup, i ->
            fadeOut(binding.goalSettingGoalWalkTimeSv)

            binding.goalSettingGoalWalkTimeBtn.apply {
                //버튼이 선택된 상태로 바꿈
                isSelected = true
                //스크롤뷰가 닫히니까 버튼의 화살표를 아래 방향으로 바꾸기
                setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                    null
                )

                when (i) {
                    binding.goalSetting15minRb.id -> {
                        text = binding.goalSetting15minRb.text.toString()
                        userModel.goalWalkTime = 15
                    }
                    binding.goalSetting30minRb.id -> {
                        text = binding.goalSetting30minRb.text.toString()
                        userModel.goalWalkTime = 30
                    }
                    binding.goalSetting60minRb.id -> {
                        text = binding.goalSetting60minRb.text.toString()
                        userModel.goalWalkTime = 60
                    }
                    binding.goalSetting90minRb.id -> {
                        text = binding.goalSetting90minRb.text.toString()
                        userModel.goalWalkTime = 90
                    }
                    binding.goalSettingDirectSettingRb.id -> {
                        //우선 초기 상태로 돌려놨다가 목표 산책 시간 다이얼로그 화면에서 설정하면 그때 UI 업데이트
                        if (!walkTimeDialogFragment.isAdded) {
                            userModel.goalWalkTime = 0

                            radioGroup.clearCheck()

                            text = getString(R.string.msg_set_goal_time)
                            isSelected = false

                            walkTimeDialogFragment.show(
                                requireActivity().supportFragmentManager,
                                null
                            )
                        }
                    }
                }
            }

            validate()
        }

        //산책 시간대 라디오그룹 체크 리스너
        binding.goalSettingWalkSlotRg.setOnCheckedChangeListener { radioGroup, i ->
            fadeOut(binding.goalSettingWalkSlotSv)

            binding.goalSettingWalkSlotBtn.apply {
                //버튼이 선택된 상태로 바꿈
                isSelected = true
                //스크롤뷰가 닫히니까 버튼의 화살표를 아래 방향으로 바꾸기
                setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                    null
                )

                when (i) {
                    binding.goalSettingEarlyMorningRb.id -> {
                        text = binding.goalSettingEarlyMorningRb.text.toString()
                        userModel.walkTimeSlot = 1
                    }
                    binding.goalSettingLateMorningRb.id -> {
                        text = binding.goalSettingLateMorningRb.text.toString()
                        userModel.walkTimeSlot = 2
                    }
                    binding.goalSettingEarlyAfternoonRb.id -> {
                        text = binding.goalSettingEarlyAfternoonRb.text.toString()
                        userModel.walkTimeSlot = 3
                    }
                    binding.goalSettingLateAfternoonRb.id -> {
                        text = binding.goalSettingLateAfternoonRb.text.toString()
                        userModel.walkTimeSlot = 4
                    }
                    binding.goalSettingNightRb.id -> {
                        text = binding.goalSettingNightRb.text.toString()
                        userModel.walkTimeSlot = 5
                    }
                    binding.goalSettingDawnRb.id -> {
                        text = binding.goalSettingDawnRb.text.toString()
                        userModel.walkTimeSlot = 6
                    }
                    binding.goalSettingDifferentEveryTimeRb.id -> {
                        text = binding.goalSettingDifferentEveryTimeRb.text.toString()
                        userModel.walkTimeSlot = 7
                    }
                }
            }

            validate()
        }

        //완료 버튼 클릭 리스너
        binding.registerGoalActionBtn.setOnClickListener {
            Log.d("RegisterGoalFragment", "완료! -> $userModel")

            //정보 등록 API 호출
            InfoService.registerInfos(this, userModel)
        }
    }

    //모든 데이터가 입력됐는지 유효성 검사 함수
    private fun validate() {
        binding.registerGoalActionBtn.isEnabled =
            userModel.goalDay.isNotEmpty() && userModel.goalWalkTime != null && userModel.walkTimeSlot != null
    }

    //InfoFragment 로부터 user 데이터를 전달 받는 함수
    fun deliverUser(userModel: UserModel) {
        this.userModel = userModel
    }


    /*정보 등록 API -> Response*/
    override fun onRegisterLoading() {}

    override fun onRegisterSuccess(result: String?) {
        Log.d("REGISTER/API-SUCCESS", "성공" + result.toString())

            //MainActivity 로 이동(MainActivity 에서 뒤로 갔을 때 RegisterActivity 로 이동하지 않도록 flag 설정)
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(Intent(intent))
    }

    override fun onRegisterFailure(code: Int, message: String) {
        Log.d("REGISTER/API-FAILURE", "code: $code message: $message")
    }

}