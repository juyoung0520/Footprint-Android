package com.footprint.footprint.ui.main.mypage

import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentGoalNextMonthBinding
import com.footprint.footprint.domain.model.GoalEntity
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.DayRVAdapter
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceWidth
import com.footprint.footprint.viewmodel.GoalViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoalNextMonthFragment :
    BaseFragment<FragmentGoalNextMonthBinding>(FragmentGoalNextMonthBinding::inflate) {
    private lateinit var dayRVAdapter: DayRVAdapter
    private lateinit var goal: GoalEntity
    private lateinit var networkErrorSb: Snackbar

    private val goalVm: GoalViewModel by viewModel()

    override fun initAfterBinding() {
        observe()

        binding.goalNextMonthPb.visibility = View.VISIBLE
        goalVm.getNextMonthGoal()

        setMyClickListener()
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrorSb.isInitialized && networkErrorSb.isShown)
            networkErrorSb.dismiss()
    }

    private fun initAdapter() {
        val deviceWidth = getDeviceWidth()
        dayRVAdapter = DayRVAdapter((deviceWidth - convertDpToPx(requireContext(), 88)) / 7)
        dayRVAdapter.setUserGoalDay(goal.dayIdx)   //어댑터에 사용자 목표 요일 데이터 전달
        dayRVAdapter.setEnabled(false)  //아이템뷰 비활성화

        binding.goalNextMonthGoalDayRv.adapter = dayRVAdapter
    }

    private fun bind() {
        //다음달 년도, 월 구해서 화면에 보여주기
        val monthList = goal.month!!.split(" ")
        binding.goalNextMonthYearTv.text = monthList[0]
        binding.goalNextMonthMonthTv.text = " ${monthList[1]}"

        //목표 산책 시간
        val hour = goal.userGoalTime.walkGoalTime!! / 60
        val minute = goal.userGoalTime.walkGoalTime!! % 60
        binding.goalNextMonthGoalWalkTimeBtn.text = when {
            hour==0 -> "${minute}분"
            minute==0 -> "${hour}시간"
            else -> "${hour}시간 ${minute}분"
        }

        //산책 시간대
        binding.goalNextMonthGoalWalkSlotBtn.text = when(goal.userGoalTime.walkTimeSlot) {
            1 -> getString(R.string.title_early_morning)
            2 -> getString(R.string.title_late_morning)
            3 -> getString(R.string.title_early_afternoon)
            4 -> getString(R.string.title_late_afternoon)
            5 -> getString(R.string.title_night)
            6 -> getString(R.string.title_dawn)
            else -> getString(R.string.title_different_every_time)
        }
    }

    private fun setMyClickListener() {
        //뒤로가기 아이콘 이미지뷰 클릭 리스너
        binding.goalNextMonthBackIv.setOnClickListener {
            (requireActivity()).onBackPressed()
        }

        //수정 텍스트뷰 클릭 리스너 -> GoalNextMonthUpdateFragment 로 이동
        binding.goalNextMonthUpdateTv.setOnClickListener {
            val action = GoalNextMonthFragmentDirections.actionGoalNextMonthFragmentToGoalNextMonthUpdateFragment(Gson().toJson(goal))
            findNavController().navigate(action)
        }
    }

    private fun observe() {
        goalVm.mutableErrorType.observe(viewLifecycleOwner, Observer {
            binding.goalNextMonthPb.visibility = View.INVISIBLE

            when (it) {
                ErrorType.NETWORK -> {
                    networkErrorSb = Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) { goalVm.getNextMonthGoal() }
                    networkErrorSb.show()
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    startErrorActivity("GoalNextMonthFragment")
                }
            }
        })

        goalVm.nextMonthGoal.observe(viewLifecycleOwner, Observer {
            LogUtils.d("GoalRepositoryImpl", "nextMonthGoal Observe!! nextMonthGoal: $it")

            binding.goalNextMonthPb.visibility = View.INVISIBLE

            this@GoalNextMonthFragment.goal = it
            initAdapter()  //어댑터 초기화
            bind()  //유저 데이터 바인딩
        })
    }
}