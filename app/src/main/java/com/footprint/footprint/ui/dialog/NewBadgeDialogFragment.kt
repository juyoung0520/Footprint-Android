package com.footprint.footprint.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.footprint.footprint.data.remote.badge.BadgeInfo
import com.footprint.footprint.databinding.FragmentNewBadgeDialogBinding
import com.footprint.footprint.utils.DialogFragmentUtils
import com.footprint.footprint.utils.loadSvg
import com.google.gson.Gson

class NewBadgeDialogFragment : DialogFragment() {

    interface MyDialogCallback {
        fun confirm()
    }

    private lateinit var binding: FragmentNewBadgeDialogBinding
    private lateinit var myDialogCallback: MyDialogCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewBadgeDialogBinding.inflate(inflater, container, false)

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        //이전 화면으로부터 전달받은 뱃지 정보를 가져다가 화면에 보여준다.
        val badge = Gson().fromJson(arguments?.getString("badge"), BadgeInfo::class.java)
        bindBadge(badge)

        //확인 텍스트뷰 클릭 리스너
        binding.newBadgeConfirmTv.setOnClickListener {
            dismiss()   //프래그먼트 종료

            //콜백함수가 등록돼 있으면 confirm 메서드 호출(아마 HomeFragment 에서는 안 쓰고 WalkAfterActivity 에서만 사용될 듯)
            if (::myDialogCallback.isInitialized)
                myDialogCallback.confirm()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        DialogFragmentUtils.dialogFragmentResize(
            requireContext(),
            this@NewBadgeDialogFragment,
            0.9f,
            0.35f
        )
    }

    private fun bindBadge(badge: BadgeInfo) {
        binding.newBadgeBadgeIv.loadSvg(requireContext(), badge.badgeUrl)
        binding.newBadgeTitleTv.text = "'${badge.badgeName}'"
    }

    fun setMyDialogCallback(myDialogCallback: MyDialogCallback) {
        this.myDialogCallback = myDialogCallback
    }

}