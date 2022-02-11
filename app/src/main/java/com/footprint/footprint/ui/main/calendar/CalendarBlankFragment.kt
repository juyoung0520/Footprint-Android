package com.footprint.footprint.ui.main.calendar

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentCalendarBlankBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.utils.getCrackStatus
import com.footprint.footprint.utils.getPWDstatus
import com.footprint.footprint.utils.saveCrackStatus

class CalendarBlankFragment : BaseFragment<FragmentCalendarBlankBinding>(FragmentCalendarBlankBinding::inflate) {

    override fun initAfterBinding() {
        lockUnlock()    //잠금 상태 확인 함수 호출
    }

    /*잠금 상태 확인*/
    private fun lockUnlock() {
        val pwdStatus = getPWDstatus()
        if (pwdStatus == "ON") {    //잠금 있을 때
            when (getCrackStatus()) {
                "SUCCESS" -> {  //LockActivity 에서 암호를 푼 상태 -> CalendarFragment 로 이동
                    Log.d("CalendarBlankFragment", "암호 풀림")
                    saveCrackStatus("NOTHING")
                    findNavController().navigate(R.id.action_calendarBlankFragment_to_calendarFragment)
                }
                "CANCEL" -> {   //LockActivity 에서 암호를 풀려다가 뒤로가기로 나온 상태 -> CalendarBlankFragment 종료하고 이전 프래그먼트로 이동
                    Log.d("CalendarBlankFragment", "암호 풀려다 취소")
                    saveCrackStatus("NOTHING")
                    findNavController().popBackStack()
                }
                else -> {   //처음 CalendarBlankFragment 에 들어온 상태 -> LockActivity 로 이동
                    Log.d("CalendarBlankFragment", "캘린더 프래그먼트 처음 들어옴")
                    val action = CalendarBlankFragmentDirections.actionCalendarBlankFragmentToLockActivity("UNLOCK")
                    findNavController().navigate(action)
                }
            }
        } else {    // 잠금 없음
            Log.d("CalendarBlankFragment", "잠금 없음") //잠금이 없을 때 -> CalendarFragment 로 이동
            findNavController().navigate(R.id.action_calendarBlankFragment_to_calendarFragment)
        }
    }
}