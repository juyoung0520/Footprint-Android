package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemFilterBinding

class CourseFilterRVAdapter(): RecyclerView.Adapter<CourseFilterRVAdapter.ViewHolder>() {
    private val filters = arrayListOf<String>()

    inner class ViewHolder(val binding: ItemFilterBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            binding.itemFilterTv.text = "#" + filters[position]
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = filters.size

    /* 데이터 관련 함수: add, delete*/
    fun updateData(datas: ArrayList<String>){
        filters.clear()
        filters.addAll(datas)

        notifyDataSetChanged()
    }
}