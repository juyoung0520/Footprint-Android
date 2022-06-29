package com.footprint.footprint.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.databinding.FragmentNoticeDialogBinding
import com.footprint.footprint.ui.main.home.HomeFragmentDirections
import com.footprint.footprint.ui.setting.NoticeDetailFragmentDirections
import com.footprint.footprint.utils.DialogFragmentUtils
import com.footprint.footprint.utils.addReadNoticeList
import com.google.gson.Gson


class NoticeDialogFragment() : DialogFragment() {
    private lateinit var binding: FragmentNoticeDialogBinding
    private lateinit var notice: NoticeDto

    private lateinit var myDialogCallback: MyDialogCallback

    interface MyDialogCallback {
        fun isDismissed()
        fun showingDetail()
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

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        notice = Gson().fromJson(arguments?.getString("notice"), NoticeDto::class.java)
        bindNotice(notice)

        // 취소 클릭 시,
        binding.noticeDialogCloseTv.setOnClickListener {
            dismiss()
            myDialogCallback.isDismissed()
        }

        // 자세히 보기 클릭 시,
        binding.noticeDialogDetailTv.setOnClickListener {
            addReadNoticeList(notice.noticeIdx) // 읽은 공지사항 리스트에 추가

            dismiss()
            myDialogCallback.isDismissed()

            // 개별 공지 화면으로 이동
            lateinit var action: NavDirections

            if (findNavController().currentDestination!!.id == R.id.homeFragment) // 홈프래그먼트 -> 자세히 보기
                action = HomeFragmentDirections.actionHomeFragmentToNoticeDetailFragment(notice.noticeIdx.toString())
            else if (findNavController().currentDestination!!.id == R.id.noticeDetailFragment) // 자세히 보기 -> 자세히 보기
                action = NoticeDetailFragmentDirections.actionNoticeDetailFragmentToNoticeDetailFragment(notice.noticeIdx.toString())

            findNavController().navigate(action)
            myDialogCallback.showingDetail()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        DialogFragmentUtils.dialogFragmentResize(
            requireContext(),
            this,
            0.9f,
            0.5f
        )

    }

    private fun bindNotice(notice: NoticeDto) {
        binding.noticeDialogTitleTv.text = notice.title
        binding.noticeDialogContentTv.text = notice.notice
    }


}