package com.footprint.footprint.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.footprint.footprint.databinding.FragmentBottomDialogOrderbyBinding
import com.footprint.footprint.domain.model.FilteringModel
import com.footprint.footprint.ui.adapter.OrderByListRVAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class OrderByBottomDialog: BottomSheetDialogFragment() {
    private lateinit var orderBy: FilteringModel
    private var selected  = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomDialogOrderbyBinding.inflate(inflater, container, false)

        orderBy = Gson().fromJson(arguments?.getString("orderBy"), FilteringModel::class.java)
        selected = arguments?.getInt("selected", 0) ?: 0 // 정렬 기준은 초기값 0

        binding.bottomDialogOrderbyTitleTv.text = orderBy.title

        val listRVAdapter = OrderByListRVAdapter(orderBy.contents, selected)
        binding.bottomDialogOrderbyRv.adapter = listRVAdapter
        listRVAdapter.setListClickListener(object : OrderByListRVAdapter.OnListClickListener{
            override fun onClick(orderBy: Int) {
                dismiss()
                onDismissListener.onDismiss(orderBy)
            }
        })

        return binding.root
    }

    /* 클릭 리스너 */
    interface OnDismissListener{
        fun onDismiss(selected: Int)
    }

    private lateinit var onDismissListener: OnDismissListener

    fun setMyItemClickListener(onDismissListener: OnDismissListener) {
        this.onDismissListener = onDismissListener
    }

    override fun getTheme(): Int {
        return com.footprint.footprint.R.style.AppBottomSheetDialogTheme
    }
}