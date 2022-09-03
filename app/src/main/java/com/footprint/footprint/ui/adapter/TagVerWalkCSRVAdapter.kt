package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemTagVerWalkCsBinding

class TagVerWalkCSRVAdapter(private val items: MutableList<String>): RecyclerView.Adapter<TagVerWalkCSRVAdapter.TagVerWalkCSViewHolder>() {
    private lateinit var binding: ItemTagVerWalkCsBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagVerWalkCSRVAdapter.TagVerWalkCSViewHolder {
        binding = ItemTagVerWalkCsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TagVerWalkCSViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TagVerWalkCSRVAdapter.TagVerWalkCSViewHolder,
        position: Int
    ) {
        holder.tv.text = items[position]
    }

    override fun getItemCount(): Int = items.size

    inner class TagVerWalkCSViewHolder(itemView: ItemTagVerWalkCsBinding): RecyclerView.ViewHolder(itemView.root) {
        val tv: TextView = itemView.root
    }
}