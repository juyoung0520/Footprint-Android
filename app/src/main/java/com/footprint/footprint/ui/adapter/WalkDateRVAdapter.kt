package com.footprint.footprint.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.data.dto.TagWalksDTO
import com.footprint.footprint.databinding.ItemWalkDateBinding

class WalkDateRVAdapter(val context: Context) : RecyclerView.Adapter<WalkDateRVAdapter.WalkDateViewHolder>() {
    private val tagWalks = arrayListOf<TagWalksDTO>()
    private lateinit var currentTag: String

    private lateinit var onWalkClickListener: WalkRVAdapter.OnItemClickListener

    @SuppressLint("NotifyDataSetChanged")
    fun setWalkDates(walkDates: List<TagWalksDTO>) {
        this.tagWalks.clear()
        this.tagWalks.addAll(walkDates)

        notifyDataSetChanged()
    }

    fun setCurrentTag(tag: String) {
        currentTag = tag
    }

    fun setWalkClickListener(listener: WalkRVAdapter.OnItemClickListener) {
        onWalkClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkDateViewHolder {
        val binding =
            ItemWalkDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WalkDateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalkDateViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return tagWalks.size
    }

    inner class WalkDateViewHolder(val binding: ItemWalkDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.walkDateDateTv.text = tagWalks[position].walkAt

            val adapter = WalkRVAdapter(context)
            //adapter.setWalks(walkDates[position].walks)
            adapter.setCurrentTag(currentTag)

            adapter.setOnItemClickListener(onWalkClickListener)

            binding.walkDateWalksRv.adapter = adapter
        }
    }
}