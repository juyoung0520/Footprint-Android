package com.footprint.footprint.ui.main.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentMyBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyBottomSheetDialogFragment : BottomSheetDialogFragment() {
    interface Callback {
        fun show()
        fun update()
        fun delete()
    }

    private lateinit var binding: FragmentMyBottomSheetDialogBinding
    private lateinit var callback: Callback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBottomSheetDialogBinding.inflate(inflater, container, false)

        setEventListener()

        return binding.root
    }

    override fun getTheme(): Int = R.style.BottomSheetDialog

    private fun setEventListener() {
        binding.myBsdViewCourseTv.setOnClickListener {  //코스 보기
            callback.show()
            dismiss()
        }

        binding.myBsdUpdateCourseTv.setOnClickListener {    //코스 수정
            callback.update()
            dismiss()
        }

        binding.myBsdDeleteCourseTv.setOnClickListener {    //코스 삭제
            callback.delete()
            dismiss()
        }
    }

    fun setMyCallback(callback: Callback) {
        this.callback = callback
    }
}