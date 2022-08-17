package com.footprint.footprint.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentTempWalkDialogBinding
import com.footprint.footprint.utils.DialogFragmentUtils

class TempWalkDialogFragment : DialogFragment() {
    interface MyCallbackListener {
        fun delete()
        fun followUp()
    }

    private lateinit var binding: FragmentTempWalkDialogBinding
    private lateinit var myCallbackListener: MyCallbackListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTempWalkDialogBinding.inflate(inflater, container, false)

        dialog?.setCancelable(false)    //외부 클릭해서 다이얼로그 닫히는거 방지하기

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        //임시 저장 메세지 특정 부분 텍스트 색상 변경
        val ssb: SpannableStringBuilder = SpannableStringBuilder(getString(R.string.msg_temp_walk))
        ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary)), 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tempWalkMsgTv.text = ssb

        setMyEventListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        DialogFragmentUtils.dialogFragmentResizeWidth(requireContext(), this@TempWalkDialogFragment, 0.87f)
    }

    private fun setMyEventListener() {
        binding.tempWalkNoProblemTv.setOnClickListener {
            myCallbackListener.delete()
            dismiss()
        }

        binding.tempWalkFollowUpTv.setOnClickListener {
            myCallbackListener.followUp()
            dismiss()
        }
    }

    fun setMyCallbackListener(myCallbackListener: MyCallbackListener) {
        this.myCallbackListener = myCallbackListener
    }
}