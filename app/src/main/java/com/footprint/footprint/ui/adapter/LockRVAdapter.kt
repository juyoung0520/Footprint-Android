package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemLockNumberBinding

class LockRVAdapter(private val itemWidthPx: Int) : RecyclerView.Adapter<LockRVAdapter.ViewHolder>() {
    private var dataList: ArrayList<Int> = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 100, 0, 200)

    /*클릭 이벤트 처리 위한 인터페이스*/
    interface OnItemClickListener {
        fun onClick(data: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemLockNumberBinding =
            ItemLockNumberBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        params.width = itemWidthPx

        holder.itemView.layoutParams = params
        holder.itemView.requestApplyInsets()

        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(val binding: ItemLockNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Int) {
            when (data) {
                100 -> {
                    //왼쪽 blank
                    binding.itemLockNumberTv.visibility = View.INVISIBLE
                    binding.lockNumberRemoveBtnIv.visibility = View.INVISIBLE
                }
                200 -> {
                    //오른쪽 remove 버튼
                    binding.itemLockNumberTv.visibility = View.INVISIBLE
                    binding.lockNumberRemoveBtnIv.visibility = View.VISIBLE
                }
                else -> {
                    //이외 다른
                    binding.itemLockNumberTv.visibility = View.VISIBLE
                    binding.lockNumberRemoveBtnIv.visibility = View.INVISIBLE
                    binding.itemLockNumberTv.text = data.toString()
                }
            }


            //클릭 시 (100, 빈칸 제외) -> LockActivity 숫자 전달
            itemView.setOnClickListener {
                if(data != 100)
                    itemClickListener.onClick(data)
            }

        }

    }
}