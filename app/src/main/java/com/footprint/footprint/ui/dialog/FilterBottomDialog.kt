package com.footprint.footprint.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.footprint.footprint.databinding.FragmentBottomDialogFilterBinding
import com.footprint.footprint.domain.model.FilteringModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams


class FilterBottomDialog: BottomSheetDialogFragment() {
    private lateinit var filter: FilteringModel
    private var selected: Int?  = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomDialogFilterBinding.inflate(inflater, container, false)

        filter = Gson().fromJson(arguments?.getString("filter"), FilteringModel::class.java)
        selected = arguments?.getInt("selected", 0) ?: 0 // 초기값 0

        binding.bottomDialogFilterTitleTv.text = filter.title
        initSeekbar(binding)

        // 적용하기 버튼 클릭 시,
        binding.bottomDialogFilterApplyBtn.setOnClickListener{
            dismiss()
            onDismissListener.onSelected(selected!!)
        }

        binding.bottomDialogFilterResetIv.setOnClickListener {
            dismiss()
            onDismissListener.onReset()
        }

        return binding.root
    }

    private fun initSeekbar(binding: FragmentBottomDialogFilterBinding){
        val ticks = filter.contents.size
        binding.bottomDialogFilterSeekbarSb.tickCount = ticks

        if(selected == null){
            selected = 0
            binding.bottomDialogSelectedTv.text = filter.contents[0]
            binding.bottomDialogFilterSeekbarSb.setProgress(0f)
        }
        else{
            val progress = (100/ticks) * selected!!.toFloat()
            binding.bottomDialogSelectedTv.text = filter.contents[selected!!]
            binding.bottomDialogFilterSeekbarSb.setProgress(progress)
        }

        when(ticks){
            5 -> {
                binding.bottomDialogFilterTick3Tv.visibility = View.VISIBLE
                binding.bottomDialogFilterTick1Tv.text = filter.units!![0]
                binding.bottomDialogFilterTick2Tv.text = filter.units!![1]
                binding.bottomDialogFilterTick3Tv.text = filter.units!![2]
                binding.bottomDialogFilterTick4Tv.text = filter.units!![3]
                binding.bottomDialogFilterTick5Tv.text = filter.units!![4]
            }
            4 -> {
                binding.bottomDialogFilterTick3Tv.visibility = View.GONE
                binding.bottomDialogFilterTick1Tv.text = filter.units!![0]
                binding.bottomDialogFilterTick2Tv.text = filter.units!![1]
                binding.bottomDialogFilterTick4Tv.text = filter.units!![2]
                binding.bottomDialogFilterTick5Tv.text = filter.units!![3]
            }
        }

        binding.bottomDialogFilterSeekbarSb.onSeekChangeListener = object : OnSeekChangeListener{
            override fun onSeeking(seekParams: SeekParams?) {
                val position = seekParams?.thumbPosition ?: 0
                selected = position
                binding.bottomDialogSelectedTv.text = filter.contents[position]
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {}
        }
    }


    /* 클릭 리스너 */
    interface OnDismissListener{
        fun onSelected(selected: Int)
        fun onReset()
    }

    private lateinit var onDismissListener: OnDismissListener

    fun setMyItemClickListener(onDismissListener: OnDismissListener) {
        this.onDismissListener = onDismissListener
    }
}