package com.footprint.footprint.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentMsgDialogBinding
import com.footprint.footprint.utils.DialogFragmentUtils

class MsgDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentMsgDialogBinding
    private val args: MsgDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMsgDialogBinding.inflate(inflater, container, false)

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        DialogFragmentUtils.dialogFragmentResize(
            requireContext(),
            this@MsgDialogFragment,
            0.9f,
            0.19f
        )

        initUI()
        setMyClickListener()
    }

    private fun initUI() {
        binding.msgDialogMsgTv.text = args.msg
    }

    private fun setMyClickListener() {
        binding.msgDialogConfirmTv.setOnClickListener {
            dismiss()
        }
    }
}