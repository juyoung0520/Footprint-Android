package com.footprint.footprint.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.classes.NonNullMutableLiveData
import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.databinding.ItemWalkBinding

class WalkRVAdapter: RecyclerView.Adapter<WalkRVAdapter.WalkViewHolder>() {
    private val walks = arrayListOf<WalkModel>()

    private lateinit var mOnItemClickListener: OnItemClickListener
    private lateinit var mOnItemRemoveClickListener: OnItemRemoveClickListener


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemRemoveClickListener {
        fun onItemRemoveClick()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setWalks(walks: ArrayList<WalkModel>) {
        this.walks.clear()
        this.walks.addAll(walks)

        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    fun setOnItemRemoveClickListener(listener: OnItemRemoveClickListener) {
        mOnItemRemoveClickListener = listener
    }

    fun addWalk(walk: WalkModel) {
        walks.add(walk)
        notifyDataSetChanged()
    }

    fun removeWalk(position: Int) {
        if(walks.isEmpty() || position !in 0 .. walks.size) {
            return
        }

        walks.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkViewHolder {
        val binding = ItemWalkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WalkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalkViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return walks.size
    }

    inner class WalkViewHolder(val binding: ItemWalkBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.walkNthRecordTv.text = walks[position].walkIndex.toString()

            binding.root.setOnClickListener {
                mOnItemClickListener.onItemClick(position)
            }

            binding.walkRemoveTv.setOnClickListener {
                removeWalk(position)
                mOnItemRemoveClickListener.onItemRemoveClick()
            }
        }
    }
}