package com.footprint.footprint.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentActionDialogBinding
import com.footprint.footprint.utils.DialogFragmentUtils

class ActionDialogFragment() : DialogFragment() {
    private lateinit var binding: FragmentActionDialogBinding
    private lateinit var myDialogCallback: MyDialogCallback
    private lateinit var msg: String    //이전 화면으로부터 전달받는 메세지(ex.실시간 기록을 중지할까요?)
    private lateinit var leftAction: String
    private lateinit var rightAction: String
    private lateinit var desc: String

    //다이얼로그 콜백 인터페이스
    interface MyDialogCallback {
        fun leftAction(action: String)
        fun rightAction(action: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msg = arguments?.getString("msg").toString()
        leftAction = arguments?.getString("left").toString()
        rightAction = arguments?.getString("right").toString()
        desc = arguments?.getString("desc", "").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActionDialogBinding.inflate(inflater, container, false)

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        setMyClickListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        DialogFragmentUtils.dialogFragmentResize(
            requireContext(),
            this@ActionDialogFragment,
            0.9f,
            0.24f
        )

        initUI()
    }

    private fun initUI() {
        binding.walkDialogMsgTv.text = msg
        binding.walkDialogCancelTv.text = leftAction
        binding.walkDialogActionTv.text = rightAction

        if (desc.isBlank()) {
            binding.walkDialogDescTv.visibility = View.GONE
        } else {
            binding.walkDialogDescTv.visibility = View.VISIBLE
            binding.walkDialogDescTv.text = desc
        }

        if (msg == getString(R.string.msg_withdrawal)) {
            binding.walkDialogCancelTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary
                )
            )
            binding.walkDialogActionTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black_dark
                )
            )
        }
    }

    private fun setMyClickListener() {
        //취소 텍스트뷰 클릭 리스너
        binding.walkDialogCancelTv.setOnClickListener {
            myDialogCallback.leftAction((it as TextView).text.toString())
            dismiss()
        }

        //액션 텍스트뷰(중지, 저장, 삭제 등) 클릭 리스너
        binding.walkDialogActionTv.setOnClickListener {
            myDialogCallback.rightAction((it as TextView).text.toString())
            dismiss()
        }
    }

    fun setMyDialogCallback(myDialogCallback: MyDialogCallback) {
        this.myDialogCallback = myDialogCallback
    }
}