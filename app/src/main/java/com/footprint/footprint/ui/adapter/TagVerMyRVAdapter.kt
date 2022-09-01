package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemTagVerMyBinding

class TagVerMyRVAdapter: RecyclerView.Adapter<TagVerMyRVAdapter.TagVerMyViewHolder>() {
    private lateinit var binding: ItemTagVerMyBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagVerMyRVAdapter.TagVerMyViewHolder {
        binding = ItemTagVerMyBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TagVerMyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagVerMyRVAdapter.TagVerMyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 7

    inner class TagVerMyViewHolder(binding: ItemTagVerMyBinding): RecyclerView.ViewHolder(binding.root) {
    }
}