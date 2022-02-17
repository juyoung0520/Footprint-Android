package com.footprint.footprint.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentWalkTimeDialogBinding
import com.footprint.footprint.utils.DialogFragmentUtils

class WalkTimeDialogFragment() : DialogFragment() {

    interface MyDialogCallback {
        fun complete(timeStr: String, minute: Int)
    }

    private lateinit var binding: FragmentWalkTimeDialogBinding
    private lateinit var myDialogCallback: MyDialogCallback

    private val hourNumList = (0..4).toList().map { it.toString() }
    private var minNumList = listOf<String>("0", "10", "20", "30", "40", "50")

    //시간 NumberPicker 변경 리스너 -> 4시 이면 선택할 수 있는 분은 0분 딱 하나
    private val hourNpListener = NumberPicker.OnValueChangeListener { numberPicker, i, i2 ->
        binding.walkTimeMinuteNp.maxValue = 0

        minNumList = if (hourNumList[i2].toInt()==4)
            listOf<String>("0")
        else
            listOf<String>("0", "10", "20", "30", "40", "50")

        binding.walkTimeMinuteNp.displayedValues = minNumList.toTypedArray()
        binding.walkTimeMinuteNp.maxValue = minNumList.size - 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWalkTimeDialogBinding.inflate(inflater, container, false)

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        initNumberPicker()
        setMyClickListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        DialogFragmentUtils.dialogFragmentResize(
            requireContext(),
            this@WalkTimeDialogFragment,
            0.9f,
            0.32f
        )
    }

    private fun initNumberPicker() {
        binding.walkTimeHourNp.displayedValues = hourNumList.toTypedArray()
        binding.walkTimeHourNp.minValue = 0
        binding.walkTimeHourNp.maxValue = hourNumList.size - 1
        binding.walkTimeHourNp.wrapSelectorWheel = true
        binding.walkTimeHourNp.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.walkTimeHourNp.setOnValueChangedListener(hourNpListener)

        binding.walkTimeMinuteNp.displayedValues = minNumList.toTypedArray()
        binding.walkTimeMinuteNp.minValue = 0
        binding.walkTimeMinuteNp.maxValue = minNumList.size - 1
        binding.walkTimeMinuteNp.wrapSelectorWheel = true
        binding.walkTimeMinuteNp.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }

    private fun setMyClickListener() {
        //취소 텍스트뷰 클릭 리스너
        binding.walkTimeDialogCancelTv.setOnClickListener {
            dismiss()
        }

        //완료 텍스트뷰 클릭 리스너
        binding.walkTimeDialogCompleteTv.setOnClickListener {
            val hour = hourNumList[binding.walkTimeHourNp.value].toInt()
            val minute = minNumList[binding.walkTimeMinuteNp.value].toInt()

            if (hour == 0 && minute == 0)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_set_goal_walk_time),
                    Toast.LENGTH_SHORT
                ).show()
            else if (hour == 0) {
                myDialogCallback.complete("${minute}분", hour*60+minute)
                dismiss()
            } else if (minute == 0) {
                myDialogCallback.complete("${hour}시간", hour*60+minute)
                dismiss()
            } else {
                myDialogCallback.complete("${hour}시간 ${minute}분", hour*60+minute)
                dismiss()
            }
        }
    }

    fun setMyDialogCallback(myDialogCallback: MyDialogCallback) {
        this.myDialogCallback = myDialogCallback
    }

}