package com.footprint.footprint.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ItemOrderbyListBinding

class OrderByListRVAdapter(val list: ArrayList<String>, var selectedIdx: Int): RecyclerView.Adapter<OrderByListRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderbyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.binding.root.setOnClickListener {
            if(selectedIdx != position){
                val tmp = selectedIdx
                selectedIdx = position

                notifyItemChanged(tmp)
                notifyItemChanged(position)
            }

            listClickListener.onClick(selectedIdx)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemOrderbyListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(orderBy: String){
            binding.itemOrderbyListTv.text = orderBy

            if(list[selectedIdx] == orderBy){
                binding.itemOrderbyListTv.setTextColor(Color.parseColor("#4FB8E7"))
                binding.itemOrderbyListTv.setTextAppearance(R.style.tv_headline_eb_16)
            }else{
                binding.itemOrderbyListTv.setTextColor(Color.parseColor("#241F20"))
                binding.itemOrderbyListTv.setTextAppearance(R.style.tv_subtitle_b_16)
            }
        }
    }

    /* 클릭 이벤트 처리 */
    /*클릭 이벤트 처리 위한 인터페이스*/
    interface OnListClickListener {
        fun onClick(orderBy: Int)
    }

    fun setListClickListener(onListClickListener: OnListClickListener) {
        this.listClickListener = onListClickListener
    }

    private lateinit var listClickListener : OnListClickListener
}