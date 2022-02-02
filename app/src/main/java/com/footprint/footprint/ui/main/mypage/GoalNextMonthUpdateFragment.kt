package com.footprint.footprint.ui.main.mypage

import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.footprint.footprint.R
import com.footprint.footprint.data.model.GoalModel
import com.footprint.footprint.databinding.FragmentGoalNextMonthUpdateBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.DayRVAdapter
import com.footprint.footprint.ui.dialog.WalkTimeDialogFragment
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.fadeIn
import com.footprint.footprint.utils.fadeOut
import com.footprint.footprint.utils.getDeviceWidth
import com.google.gson.Gson

class GoalNextMonthUpdateFragment : BaseFragment<FragmentGoalNextMonthUpdateBinding>(FragmentGoalNextMonthUpdateBinding::inflate) {
    private val args: GoalNextMonthUpdateFragmentArgs by navArgs()

    private lateinit var goal: GoalModel    //수정 전 목표 데이터
    private lateinit var updateGoal: GoalModel  //사용자가 수정한 목표 데이터
    private lateinit var dayRVAdapter: DayRVAdapter
    private lateinit var walkTimeDialogFragment: WalkTimeDialogFragment

    override fun initAfterBinding() {
        goal = Gson().fromJson(args.goal, GoalModel::class.java)    //이전 화면(GoalThisMonthFragment, GoalNextMonthFragment 로부터 전달 받은 goal 데이터)
        updateGoal = Gson().fromJson(args.goal, GoalModel::class.java)

        initAdapter()   //어댑터 초기화
        bind()  //데이터 바인딩
        setMyEventListener()    //이벤트 리스너 설정
        initWalkTimeDialog()    //목표 산책 시간 직접 설정 다이얼로그 화면 초기화
    }

    private fun initAdapter() {
        val deviceWidth = getDeviceWidth()
        dayRVAdapter = DayRVAdapter((deviceWidth - convertDpToPx(requireContext(), 88)) / 7)
        dayRVAdapter.setUserGoalDay(goal.dayIdx)   //어댑터에 사용자 목표 요일 데이터 전달

        dayRVAdapter.setMyItemClickListener(object : DayRVAdapter.MyItemClickListener {
            override fun saveDay(day: Int) {
                updateGoal.dayIdx.add(day)
            }

            override fun removeDay(day: Int) {
                updateGoal.dayIdx.remove(day)
            }

        })

        binding.goalNextMonthUpdateGoalDayRv.adapter = dayRVAdapter
    }

    private fun bind() {
        //현재 년도, 월 구해서 화면에 보여주기
        val monthList = goal.month.split(" ")
        binding.goalNextMonthUpdateYearTv.text = monthList[0]
        binding.goalNextMonthUpdateMonthTv.text = " ${monthList[1]}"

        //목표 산책 시간
        val hour = goal.userGoalTime.walkGoalTime!! / 60
        val minute = goal.userGoalTime.walkGoalTime!! % 60
        binding.goalNextMonthUpdateGoalWalkTimeBtn.text = when {
            hour==0 -> "${minute}분"
            minute==0 -> "${hour}시간"
            else -> "${hour}시간 ${minute}분"
        }

        //산책 시간대
        binding.goalNextMonthUpdateGoalWalkSlotBtn.text = when(goal.userGoalTime.walkTimeSlot) {
            1 -> getString(R.string.title_early_morning)
            2 -> getString(R.string.title_late_morning)
            3 -> getString(R.string.title_early_afternoon)
            4 -> getString(R.string.title_late_afternoon)
            5 -> getString(R.string.title_night)
            6 -> getString(R.string.title_dawn)
            else -> getString(R.string.title_different_every_time)
        }
    }

    private fun setMyEventListener() {
        //목표 산책 시간 버튼 클릭 리스너
        binding.goalNextMonthUpdateGoalWalkTimeBtn.setOnClickListener {
            if (binding.goalNextMonthUpdateGoalWalkTimeSv.visibility == View.GONE) {    //스크롤뷰가 GONE 상태면 스크롤뷰를 보여준다.
                fadeIn(binding.goalNextMonthUpdateGoalWalkTimeSv)

                (it as Button).setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_up_arrow),
                    null
                )
            } else {    //스크롤뷰가 VISIBLE 상태면 스크롤뷰를 감춘다.
                fadeOut(binding.goalNextMonthUpdateGoalWalkTimeSv)

                (it as Button).setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                    null
                )
            }
        }

        //산책 시간대 버튼 클릭 리스너
        binding.goalNextMonthUpdateGoalWalkSlotBtn.setOnClickListener {
            if (binding.goalNextMonthUpdateWalkSlotSv.visibility == View.GONE) {    //스크롤뷰가 GONE 상태면 스크롤뷰를 보여준다.
                fadeIn(binding.goalNextMonthUpdateWalkSlotSv)

                (it as Button).setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_up_arrow),
                    null
                )
            } else {    //스크롤뷰가 VISIBLE 상태면 스크롤뷰를 감춘다.
                fadeOut(binding.goalNextMonthUpdateWalkSlotSv)

                (it as Button).setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                    null
                )
            }
        }

        //목표 산책 시간 라디오버튼 선택 리스너
        binding.goalNextMonthUpdateGoalWalkTimeRg.setOnCheckedChangeListener { radioGroup, i ->
            fadeOut(binding.goalNextMonthUpdateGoalWalkTimeSv)  //스크롤뷰를 감춘다.

            binding.goalNextMonthUpdateGoalWalkTimeBtn.apply {
                //스크롤뷰가 닫히니까 버튼의 화살표를 아래 방향으로 바꾸기
                setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                    null
                )

                when (i) {  //사용자가 선택한 목표 시간대에 맞춰 UI 업데이트
                    binding.goalNextMonthUpdate15minRb.id -> {
                        text = binding.goalNextMonthUpdate15minRb.text.toString()
                        updateGoal.userGoalTime.walkGoalTime = 15
                    }
                    binding.goalNextMonthUpdate30minRb.id -> {
                        text = binding.goalNextMonthUpdate30minRb.text.toString()
                        updateGoal.userGoalTime.walkGoalTime = 30
                    }
                    binding.goalNextMonthUpdate60minRb.id -> {
                        text = binding.goalNextMonthUpdate60minRb.text.toString()
                        updateGoal.userGoalTime.walkGoalTime = 60
                    }
                    binding.goalNextMonthUpdate90minRb.id -> {
                        text = binding.goalNextMonthUpdate90minRb.text.toString()
                        updateGoal.userGoalTime.walkGoalTime = 90
                    }
                    binding.goalNextMonthUpdateDirectSettingRb.id -> {
                        //우선 초기 상태로 돌려놨다가 목표 산책 시간 다이얼로그 화면에서 설정하면 그때 UI 업데이트
                        if (!walkTimeDialogFragment.isAdded) {
                            updateGoal.userGoalTime.walkGoalTime = null

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
        }

        //산책 시간대 라디오그룹 체크 리스너
        binding.goalNextMonthUpdateWalkSlotRg.setOnCheckedChangeListener { radioGroup, i ->
            fadeOut(binding.goalNextMonthUpdateWalkSlotSv)

            binding.goalNextMonthUpdateGoalWalkSlotBtn.apply {
                //스크롤뷰가 닫히니까 버튼의 화살표를 아래 방향으로 바꾸기
                setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                    null
                )

                when (i) {  //사용자가 선택한 라디오버튼에 따라 UI 업데이트
                    binding.goalNextMonthUpdateEarlyMorningRb.id -> {
                        text = binding.goalNextMonthUpdateEarlyMorningRb.text.toString()
                        updateGoal.userGoalTime.walkTimeSlot = 1
                    }
                    binding.goalNextMonthUpdateLateMorningRb.id -> {
                        text = binding.goalNextMonthUpdateLateMorningRb.text.toString()
                        updateGoal.userGoalTime.walkTimeSlot = 2
                    }
                    binding.goalNextMonthUpdateEarlyAfternoonRb.id -> {
                        text = binding.goalNextMonthUpdateEarlyAfternoonRb.text.toString()
                        updateGoal.userGoalTime.walkTimeSlot = 3
                    }
                    binding.goalNextMonthUpdateLateAfternoonRb.id -> {
                        text = binding.goalNextMonthUpdateLateAfternoonRb.text.toString()
                        updateGoal.userGoalTime.walkTimeSlot = 4
                    }
                    binding.goalNextMonthUpdateNightRb.id -> {
                        text = binding.goalNextMonthUpdateNightRb.text.toString()
                        updateGoal.userGoalTime.walkTimeSlot = 5
                    }
                    binding.goalNextMonthUpdateDawnRb.id -> {
                        text = binding.goalNextMonthUpdateDawnRb.text.toString()
                        updateGoal.userGoalTime.walkTimeSlot = 6
                    }
                    binding.goalNextMonthUpdateDifferentEveryTimeRb.id -> {
                        text = binding.goalNextMonthUpdateDifferentEveryTimeRb.text.toString()
                        updateGoal.userGoalTime.walkTimeSlot = 7
                    }
                }
            }
        }

        //저장 텍스트뷰 클릭 리스너
        binding.goalNextMonthUpdateSaveTv.setOnClickListener {
            validate()  //유효성 검사
        }

        //취소 텍스트뷰 클릭 리스너
        binding.goalNextMonthUpdateCancelTv.setOnClickListener {
            findNavController().popBackStack()  //뒤로가기
        }
    }

    private fun initWalkTimeDialog() {
        walkTimeDialogFragment = WalkTimeDialogFragment()

        walkTimeDialogFragment.setMyDialogCallback(object :
            WalkTimeDialogFragment.MyDialogCallback {
            override fun complete(timeStr: String, minute: Int) {
                binding.goalNextMonthUpdateGoalWalkTimeBtn.text = timeStr

                binding.goalNextMonthUpdateGoalWalkTimeRg.check(R.id.goal_setting_direct_setting_rb)

                updateGoal.userGoalTime.walkGoalTime = minute
            }
        })
    }

    private fun validate() {
        if (updateGoal.dayIdx.isEmpty()) {  //목표 산책 요일을 선택하지 않았을 때
            val action = GoalNextMonthUpdateFragmentDirections.actionGoalNextMonthUpdateFragmentToMsgDialogFragment2(getString(R.string.error_set_goal_walk_time))
            findNavController().navigate(action)
        } else if (goal==updateGoal) {    //변경된 내역이 없을 때
            val action = GoalNextMonthUpdateFragmentDirections.actionGoalNextMonthUpdateFragmentToMsgDialogFragment2(getString(R.string.error_no_updating_goal))
            findNavController().navigate(action)
        } else {    //유효성 검사 통과
            Log.d("GoalNextMonthUpdateFragment", "updateGoal: $updateGoal")

            val action = GoalNextMonthUpdateFragmentDirections.actionGoalNextMonthUpdateFragmentToMsgDialogFragment2(getString(R.string.msg_success_update_goal))
            findNavController().navigate(action)
        }
    }
}