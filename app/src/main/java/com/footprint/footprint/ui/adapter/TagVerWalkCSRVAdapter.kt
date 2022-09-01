package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemTagVerWalkCsBinding

class TagVerWalkCSRVAdapter: RecyclerView.Adapter<TagVerWalkCSRVAdapter.TagVerWalkCSViewHolder>() {
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
    }

    override fun getItemCount(): Int = 5

    inner class TagVerWalkCSViewHolder(itemView: ItemTagVerWalkCsBinding): RecyclerView.ViewHolder(itemView.root) {

    }
}