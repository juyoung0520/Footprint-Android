package com.footprint.footprint.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentWalkDialogBinding
import com.footprint.footprint.utils.DialogFragmentUtils

class WalkDialogFragment(private val msg: String) : DialogFragment() {
    private lateinit var binding: FragmentWalkDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWalkDialogBinding.inflate(inflater, container, false)

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        initUI()
        setMyClickListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        DialogFragmentUtils.dialogFragmentResize(
            requireContext(),
            this@WalkDialogFragment,
            0.9f,
            0.24f
        )
    }

    private fun initUI() {
        binding.walkDialogMsgTv.text = msg

        binding.walkDialogActionTv.text.apply {
            when (msg) {
                "해당 기록을 삭제할까요?" -> getString(R.string.action_delete)
                "‘OO번째 발자국’을 저장할까요?" -> getString(R.string.action_save)
                "‘OO번째 발자국’ 작성을 취소할까요?" -> getString(R.string.action_delete)
            }
        }
    }

    private fun setMyClickListener() {
        binding.walkDialogCancelTv.setOnClickListener {
            dismiss()
        }

        binding.walkDialogActionTv.setOnClickListener {
            dismiss()
        }
    }
}