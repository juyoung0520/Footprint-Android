package com.footprint.footprint.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.data.remote.walk.WalkDateResult
import com.footprint.footprint.databinding.ItemWalkDateBinding

class WalkDateRVAdapter(val context: Context) : RecyclerView.Adapter<WalkDateRVAdapter.WalkDateViewHolder>() {
    private val walkDates = arrayListOf<WalkDateResult>()
    private lateinit var currentTag: String

    private lateinit var onWalkClickListener: WalkRVAdapter.OnItemClickListener

    @SuppressLint("NotifyDataSetChanged")
    fun setWalkDates(walkDates: List<WalkDateResult>) {
        this.walkDates.clear()
        this.walkDates.addAll(walkDates)

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
        return walkDates.size
    }

    inner class WalkDateViewHolder(val binding: ItemWalkDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.walkDateDateTv.text = walkDates[position].walkAt

            val adapter = WalkRVAdapter(context)
            adapter.setWalks(walkDates[position].walks)
            adapter.setCurrentTag(currentTag)

            adapter.setOnItemClickListener(onWalkClickListener)

            binding.walkDateWalksRv.adapter = adapter
        }
    }
}