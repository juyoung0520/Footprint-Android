package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.ItemCourseBinding
import com.footprint.footprint.databinding.ItemCourseTagBinding

class CourseTagRVAdapter(private val tagList: List<String>): RecyclerView.Adapter<CourseTagRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCourseTagBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: String) {
            binding.itemCourseTagTv.text = "#$tag"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCourseTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tagList[position])
    }

    override fun getItemCount(): Int = tagList.size

}