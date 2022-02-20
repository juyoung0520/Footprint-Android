package com.footprint.footprint.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemTagBinding
import com.footprint.footprint.utils.getTags
import com.footprint.footprint.utils.saveTags

class TagRVAdapter(val context: Context) : RecyclerView.Adapter<TagRVAdapter.TagViewHolder>() {
    private val tags = getTags(context) ?: arrayListOf()

    interface OnItemClickListener {
        fun onItemClick(tag: String)
    }

    interface OnItemRemoveClickListener {
        fun onItemRemoveClick()
    }

    private lateinit var mOnItemClickListener: OnItemClickListener
    private lateinit var mOnItemRemoveClickListener: OnItemRemoveClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    fun setOnItemRemoveClickListener(listener: OnItemRemoveClickListener) {
        mOnItemRemoveClickListener = listener
    }

    fun saveCurrentTags() {
        saveTags(context, tags)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyAdapter() {
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addTag(tag: String) {
        if (tags.contains(tag)) {
            tags.remove(tag)
        }

        tags.add(0, tag)

        if (tags.size > 10) {
            tags.removeLast()
        }

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeTag(position: Int) {
        if (tags.isEmpty() || position !in 0..tags.size) {
            return
        }

        tags.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    inner class TagViewHolder(val binding: ItemTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tagKeywordTv.text = tags[position]

            binding.root.setOnClickListener {
                mOnItemClickListener.onItemClick(tags[position])
            }

            binding.tagRemoveIv.setOnClickListener {
                removeTag(position)
                mOnItemRemoveClickListener.onItemRemoveClick()
            }
        }
    }
}