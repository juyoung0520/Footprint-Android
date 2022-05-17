package com.footprint.footprint.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.footprint.footprint.data.dto.KeyNoticeDto
import com.footprint.footprint.databinding.FragmentNoticeDialogBinding
import com.google.gson.Gson
import java.security.Key

class NoticeDialogFragment(): DialogFragment() {
    private lateinit var binding: FragmentNoticeDialogBinding
    private lateinit var myDialogCallback: MyDialogCallback

    private lateinit var notice: KeyNoticeDto

    interface MyDialogCallback {
        fun detail(notice: KeyNoticeDto)
    }

    fun setMyDialogCallback(myDialogCallback: MyDialogCallback) {
        this.myDialogCallback = myDialogCallback
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

        notice = Gson().fromJson(arguments?.getString("notice"), KeyNoticeDto::class.java)
        bindNotice(notice)

        binding.noticeDialogCloseTv.setOnClickListener {
            dismiss()
        }

        binding.noticeDialogDetailTv.setOnClickListener {
            dismiss()   //프래그먼트 종료

            //콜백함수가 등록돼 있으면 detail 메서드 호출
            if (::myDialogCallback.isInitialized)
                myDialogCallback.detail(notice)
        }

        return binding.root
    }

    private fun bindNotice(notice: KeyNoticeDto) {
        binding.noticeDialogTitleTv.text = notice.title
        binding.noticeDialogContentTv.text = notice.notice
    }


}