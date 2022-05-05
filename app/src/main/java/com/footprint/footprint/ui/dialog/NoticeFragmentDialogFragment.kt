package com.footprint.footprint.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.footprint.footprint.databinding.FragmentNoticeDialogBinding

class NoticeFragmentDialogFragment(): DialogFragment() {
    private lateinit var binding: FragmentNoticeDialogBinding
    private lateinit var myDialogCallback: NoticeFragmentDialogFragment.MyDialogCallback

    private var title: String = ""
    private var msg: String = ""


    interface MyDialogCallback {
        fun detail()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoticeDialogBinding.inflate(inflater, container, false)

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        initUI()

        binding.noticeDialogCloseTv.setOnClickListener {
            dismiss()
        }

        binding.noticeDialogDetailTv.setOnClickListener {
            dismiss()   //프래그먼트 종료

            //콜백함수가 등록돼 있으면 detail 메서드 호출
            if (::myDialogCallback.isInitialized)
                myDialogCallback.detail()
        }

        return binding.root
    }

    private fun initUI() {
        title = arguments?.getString("title", "")!!
        msg = arguments?.getString("msg", "")!!

        binding.noticeDialogTitleTv.text = title
        binding.noticeDialogContentTv.text = msg
    }
}