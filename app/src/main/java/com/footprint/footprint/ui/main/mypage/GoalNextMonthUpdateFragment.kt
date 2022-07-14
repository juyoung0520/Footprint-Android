package com.footprint.footprint.ui.main.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.GoalModel
import com.footprint.footprint.databinding.FragmentGoalNextMonthUpdateBinding
import com.footprint.footprint.domain.model.UpdateGoalEntity
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.DayRVAdapter
import com.footprint.footprint.ui.dialog.WalkTimeDialogFragment
import com.footprint.footprint.ui.error.ErrorActivity
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.GoalViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoalNextMonthUpdateFragment : BaseFragment<FragmentGoalNextMonthUpdateBinding>(FragmentGoalNextMonthUpdateBinding::inflate) {
    private val args: GoalNextMonthUpdateFragmentArgs by navArgs()
    private val goalVm: GoalViewModel by viewModel()
    private val updateGoalEntity: UpdateGoalEntity = UpdateGoalEntity()  //사용자가 수정한 목표 데이터

    private lateinit var goal: GoalModel    //수정 전 목표 데이터
    private lateinit var dayRVAdapter: DayRVAdapter
    private lateinit var walkTimeDialogFragment: WalkTimeDialogFragment
    private lateinit var networkErrSb: Snackbar

    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityResult()

        goal = Gson().fromJson(args.goal, GoalModel::class.java)    //이전 화면(GoalThisMonthFragment, GoalNextMonthFragment 로부터 전달 받은 goal 데이터)

        //수정할 데이터에 이번달 목표로 저장해놓기
        updateGoalEntity.dayIdx.addAll(goal.dayIdx)
        updateGoalEntity.walkGoalTime = goal.userGoalTime.walkGoalTime
        updateGoalEntity.walkTimeSlot = goal.userGoalTime.walkTimeSlot
    }

    override fun initAfterBinding() {
        initAdapter()   //어댑터 초기화
        bind(goal.month!!)  //데이터 바인딩
        observe()
        setMyEventListener()    //이벤트 리스너 설정
        initWalkTimeDialog()    //목표 산책 시간 직접 설정 다이얼로그 화면 초기화
    }

    /* 여기 */
    private fun initActivityResult() {
        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if(result.resultCode == ErrorActivity.RETRY){
                validate()
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }

    private fun initAdapter() {
        val deviceWidth = getDeviceWidth()
        dayRVAdapter = DayRVAdapter((deviceWidth - convertDpToPx(requireContext(), 88)) / 7)
        dayRVAdapter.setUserGoalDay(updateGoalEntity.dayIdx)   //어댑터에 사용자 목표 요일 데이터 전달

        dayRVAdapter.setMyItemClickListener(object : DayRVAdapter.MyItemClickListener {
            override fun saveDay(day: Int) {
                updateGoalEntity.dayIdx.add(day)
            }

            override fun removeDay(day: Int) {
                updateGoalEntity.dayIdx.remove(day)
            }

        })

        binding.goalNextMonthUpdateGoalDayRv.adapter = dayRVAdapter
    }

    private fun bind(month: String) {
        //현재 년도, 월 구해서 화면에 보여주기
        val monthList = month.split(" ")
        binding.goalNextMonthUpdateYearTv.text = monthList[0]
        binding.goalNextMonthUpdateMonthTv.text = " ${monthList[1]}"

        //목표 산책 시간
        val hour = updateGoalEntity.walkGoalTime!! / 60
        val minute = updateGoalEntity.walkGoalTime!! % 60
        binding.goalNextMonthUpdateGoalWalkTimeBtn.text = when {
            hour==0 -> "${minute}분"
            minute==0 -> "${hour}시간"
            else -> "${hour}시간 ${minute}분"
        }

        //산책 시간대
        binding.goalNextMonthUpdateGoalWalkSlotBtn.text = when(updateGoalEntity.walkTimeSlot) {
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
                binding.goalNextMonthUpdateGoalWalkTimeIv.setImageResource(R.drawable.ic_up_arrow)
            } else {    //스크롤뷰가 VISIBLE 상태면 스크롤뷰를 감춘다.
                fadeOut(binding.goalNextMonthUpdateGoalWalkTimeSv)
                binding.goalNextMonthUpdateGoalWalkTimeIv.setImageResource(R.drawable.ic_down_arrow)
            }
        }

        //산책 시간대 버튼 클릭 리스너
        binding.goalNextMonthUpdateGoalWalkSlotBtn.setOnClickListener {
            if (binding.goalNextMonthUpdateWalkSlotSv.visibility == View.GONE) {    //스크롤뷰가 GONE 상태면 스크롤뷰를 보여준다.
                fadeIn(binding.goalNextMonthUpdateWalkSlotSv)
                binding.goalNextMonthUpdateGoalWalkSlotIv.setImageResource(R.drawable.ic_up_arrow)
            } else {    //스크롤뷰가 VISIBLE 상태면 스크롤뷰를 감춘다.
                fadeOut(binding.goalNextMonthUpdateWalkSlotSv)
                binding.goalNextMonthUpdateGoalWalkSlotIv.setImageResource(R.drawable.ic_down_arrow)
            }
        }

        //목표 산책 시간 라디오버튼 선택 리스너
        binding.goalNextMonthUpdateGoalWalkTimeRg.setOnCheckedChangeListener { radioGroup, i ->
            fadeOut(binding.goalNextMonthUpdateGoalWalkTimeSv)  //스크롤뷰를 감춘다.
            binding.goalNextMonthUpdateGoalWalkTimeIv.setImageResource(R.drawable.ic_down_arrow)    //스크롤뷰가 닫히니까 버튼의 화살표를 아래 방향으로 바꾸기

            binding.goalNextMonthUpdateGoalWalkTimeBtn.apply {
                when (i) {  //사용자가 선택한 목표 시간대에 맞춰 UI 업데이트
                    binding.goalNextMonthUpdate15minRb.id -> {
                        text = binding.goalNextMonthUpdate15minRb.text.toString()
                        updateGoalEntity.walkGoalTime = 15
                    }
                    binding.goalNextMonthUpdate30minRb.id -> {
                        text = binding.goalNextMonthUpdate30minRb.text.toString()
                        updateGoalEntity.walkGoalTime = 30
                    }
                    binding.goalNextMonthUpdate60minRb.id -> {
                        text = binding.goalNextMonthUpdate60minRb.text.toString()
                        updateGoalEntity.walkGoalTime = 60
                    }
                    binding.goalNextMonthUpdate90minRb.id -> {
                        text = binding.goalNextMonthUpdate90minRb.text.toString()
                        updateGoalEntity.walkGoalTime = 90
                    }
                    binding.goalNextMonthUpdateDirectSettingRb.id -> {
                        //우선 초기 상태로 돌려놨다가 목표 산책 시간 다이얼로그 화면에서 설정하면 그때 UI 업데이트
                        if (!walkTimeDialogFragment.isAdded) {
                            updateGoalEntity.walkGoalTime = null

                            radioGroup.clearCheck()

                            text = getString(R.string.msg_set_goal_time)
                            isSelected = false

                            walkTimeDialogFragment.show(requireActivity().supportFragmentManager, null)
                        }
                    }
                }
            }
        }

        //산책 시간대 라디오그룹 체크 리스너
        binding.goalNextMonthUpdateWalkSlotRg.setOnCheckedChangeListener { radioGroup, i ->
            fadeOut(binding.goalNextMonthUpdateWalkSlotSv)
            binding.goalNextMonthUpdateGoalWalkSlotIv.setImageResource(R.drawable.ic_down_arrow)    //스크롤뷰가 닫히니까 버튼의 화살표를 아래 방향으로 바꾸기

            binding.goalNextMonthUpdateGoalWalkSlotBtn.apply {
                when (i) {  //사용자가 선택한 라디오버튼에 따라 UI 업데이트
                    binding.goalNextMonthUpdateEarlyMorningRb.id -> {
                        text = binding.goalNextMonthUpdateEarlyMorningRb.text.toString()
                        updateGoalEntity.walkTimeSlot = 1
                    }
                    binding.goalNextMonthUpdateLateMorningRb.id -> {
                        text = binding.goalNextMonthUpdateLateMorningRb.text.toString()
                        updateGoalEntity.walkTimeSlot = 2
                    }
                    binding.goalNextMonthUpdateEarlyAfternoonRb.id -> {
                        text = binding.goalNextMonthUpdateEarlyAfternoonRb.text.toString()
                        updateGoalEntity.walkTimeSlot = 3
                    }
                    binding.goalNextMonthUpdateLateAfternoonRb.id -> {
                        text = binding.goalNextMonthUpdateLateAfternoonRb.text.toString()
                        updateGoalEntity.walkTimeSlot = 4
                    }
                    binding.goalNextMonthUpdateNightRb.id -> {
                        text = binding.goalNextMonthUpdateNightRb.text.toString()
                        updateGoalEntity.walkTimeSlot = 5
                    }
                    binding.goalNextMonthUpdateDawnRb.id -> {
                        text = binding.goalNextMonthUpdateDawnRb.text.toString()
                        updateGoalEntity.walkTimeSlot = 6
                    }
                    binding.goalNextMonthUpdateDifferentEveryTimeRb.id -> {
                        text = binding.goalNextMonthUpdateDifferentEveryTimeRb.text.toString()
                        updateGoalEntity.walkTimeSlot = 7
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

                updateGoalEntity.walkGoalTime = minute
            }
        })
    }

    private fun validate() {
        when {
            updateGoalEntity.dayIdx.isEmpty() -> {  //목표 산책 요일을 선택하지 않았을 때
                val action = GoalNextMonthUpdateFragmentDirections.actionGoalNextMonthUpdateFragmentToMsgDialogFragment2(getString(R.string.error_set_goal_walk_time))
                findNavController().navigate(action)
            }
            isSame() -> {    //변경된 내역이 없을 때
                val action = GoalNextMonthUpdateFragmentDirections.actionGoalNextMonthUpdateFragmentToMsgDialogFragment2(getString(R.string.error_no_updating_goal))
                findNavController().navigate(action)
            }
            else -> {    //유효성 검사 통과
                goalVm.updateGoal(goal.month!!, updateGoalEntity)
                binding.goalNextMonthUpdatePb.visibility = View.VISIBLE
            }
        }
    }

    private fun isSame(): Boolean {
        return goal.dayIdx==updateGoalEntity.dayIdx && goal.userGoalTime.walkGoalTime==updateGoalEntity.walkGoalTime && goal.userGoalTime.walkTimeSlot==updateGoalEntity.walkTimeSlot
    }

    private fun observe() {
        goalVm.mutableErrorType.observe(viewLifecycleOwner, Observer {
            binding.goalNextMonthUpdatePb.visibility = View.INVISIBLE

            when (it) {
                ErrorType.NETWORK -> {
                    networkErrSb = Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) { goalVm.updateGoal(goal.month!!, updateGoalEntity) }
                    networkErrSb.show()
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    /* 여기 */
                    startErrorActivity(getResult, "GoalNextMonthUpdateFragment")
                    //showToast(getString(R.string.error_sorry))
                    //findNavController().popBackStack()
                }
            }
        })

        goalVm.nextMonthGoal.observe(viewLifecycleOwner, Observer {
            binding.goalNextMonthUpdatePb.visibility = View.INVISIBLE
            val action = GoalNextMonthUpdateFragmentDirections.actionGoalNextMonthUpdateFragmentToMsgDialogFragment2(getString(R.string.msg_success_update_goal))
            findNavController().navigate(action)
        })
    }
}