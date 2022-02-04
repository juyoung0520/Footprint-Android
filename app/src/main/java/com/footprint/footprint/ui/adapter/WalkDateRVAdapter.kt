package com.footprint.footprint.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.data.model.WalkDateModel
import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.databinding.ItemWalkDateBinding

class WalkDateRVAdapter : RecyclerView.Adapter<WalkDateRVAdapter.WalkDateViewHolder>() {
    private val walkDates = arrayListOf<WalkDateModel>()

    private lateinit var onWalkDateRemoveListener: OnWalkDateRemoveListener

    private lateinit var onWalkClickListener: WalkRVAdapter.OnItemClickListener
    private lateinit var fragmentManager: FragmentManager

    interface OnWalkDateRemoveListener {
        fun onWalkDateRemove()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setWalkDates(walkDates: ArrayList<WalkDateModel>) {
        this.walkDates.clear()
        this.walkDates.addAll(walkDates)

        notifyDataSetChanged()
    }

    fun setWalkClickListener(listener: WalkRVAdapter.OnItemClickListener) {
        onWalkClickListener = listener
    }

    fun setWalkDateRemoveListener(listener: WalkDateRVAdapter.OnWalkDateRemoveListener) {
        this.onWalkDateRemoveListener = listener
    }

    fun setFragmentManager(fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager
    }

    private fun removeWalkDate(position: Int) {
        if (walkDates.isEmpty() || position !in 0..walkDates.size) {
            return
        }

        walkDates.removeAt(position)
        notifyDataSetChanged()
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

            val adapter = WalkRVAdapter()
            adapter.setWalks(walkDates[position].walks)

            adapter.setOnItemClickListener(onWalkClickListener)
            adapter.setOnItemRemoveClickListener(object : WalkRVAdapter.OnItemRemoveClickListener {
                override fun onItemRemoveClick() {
                    if (adapter.itemCount == 0) {
                        removeWalkDate(position)
                        onWalkDateRemoveListener.onWalkDateRemove()
                    }
                }

            })
            adapter.setFragmentManager(fragmentManager)

            binding.walkDateWalksRv.adapter = adapter
        }
    }
}