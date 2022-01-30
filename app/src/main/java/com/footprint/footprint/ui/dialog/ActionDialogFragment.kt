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
import com.footprint.footprint.databinding.FragmentActionDialogBinding
import com.footprint.footprint.utils.DialogFragmentUtils

class ActionDialogFragment() : DialogFragment() {
    private lateinit var binding: FragmentActionDialogBinding
    private lateinit var myDialogCallback: MyDialogCallback

    private lateinit var msg: String    //이전 화면으로부터 전달받는 메세지(ex.실시간 기록을 중지할까요?)

    //다이얼로그 콜백 인터페이스
    interface MyDialogCallback {
        fun action1(isAction: Boolean)
        fun action2(isAction: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msg = arguments?.getString("msg").toString()
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

        when (msg) {
            getString(R.string.msg_stop_realtime_record), getString(R.string.msg_stop_walk) -> binding.walkDialogActionTv.text =
                "중지"
            getString(R.string.action_delete), getString(R.string.msg_cancel_walk), getString(R.string.msg_delete_footprint), "'OO번째 산책' 을 삭제하시겠어요?" -> binding.walkDialogActionTv.text =
                "삭제"
            getString(R.string.msg_save_post), getString(R.string.msg_save_walk) -> binding.walkDialogActionTv.text =
                "저장"
            getString(R.string.msg_change_representative_badge) -> binding.walkDialogActionTv.text =
                getString(R.string.action_set)
        }

        if (msg.contains("산책' 을 삭제하시겠어요?")) {
            binding.walkDialogDescTv.visibility = View.VISIBLE
            binding.walkDialogDescTv.text = "*동선을 제외한 발자국이 모두 삭제되고 \n해당 기록은 복구할 수 없어요"
        } else
            binding.walkDialogDescTv.visibility = View.GONE
    }

    private fun setMyClickListener() {
        //취소 텍스트뷰 클릭 리스너
        binding.walkDialogCancelTv.setOnClickListener {
            when (msg) {
                getString(R.string.msg_delete_footprint) -> myDialogCallback.action2(false)
                else -> myDialogCallback.action1(false)
            }

            dismiss()
        }

        //액션 텍스트뷰(중지, 저장, 삭제 등) 클릭 리스너
        binding.walkDialogActionTv.setOnClickListener {
            when (msg) {
                getString(R.string.msg_delete_footprint) -> myDialogCallback.action2(true)
                else -> myDialogCallback.action1(true)
            }

            dismiss()
        }
    }

    fun setMyDialogCallback(myDialogCallback: MyDialogCallback) {
        this.myDialogCallback = myDialogCallback
    }
}