package com.footprint.footprint.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.classes.NonNullMutableLiveData
import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.databinding.ItemWalkBinding

class WalkRVAdapter: RecyclerView.Adapter<WalkRVAdapter.WalkViewHolder>() {
    private val _walks = NonNullMutableLiveData<ArrayList<WalkModel>>(arrayListOf())
    val walks: LiveData<ArrayList<WalkModel>> get() = _walks

    private lateinit var mOnItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setWalks(walks: ArrayList<WalkModel>) {
        _walks.value.clear()
        _walks.value.addAll(walks)

        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    fun addWalk(walk: WalkModel) {
        _walks.value.add(walk)
        notifyDataSetChanged()
    }

    fun removeWalk(position: Int) {
        if(_walks.value.isEmpty() || position !in 0 .. _walks.value.size) {
            return
        }

        _walks.value.removeAt(position)
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
        return _walks.value.size
    }

    inner class WalkViewHolder(val binding: ItemWalkBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.walkNthRecordTv.text = _walks.value[position].walkIndex.toString()

            binding.root.setOnClickListener {
                mOnItemClickListener.onItemClick(position)
            }

            binding.walkRemoveTv.setOnClickListener {
                removeWalk(position)
            }
        }
    }
}