package com.footprint.footprint.ui.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.databinding.ItemWalkBinding
import com.footprint.footprint.ui.dialog.ActionDialogFragment

class WalkRVAdapter() : RecyclerView.Adapter<WalkRVAdapter.WalkViewHolder>() {
    private val walks = arrayListOf<WalkModel>()

    private lateinit var mOnItemClickListener: OnItemClickListener
    private lateinit var mOnItemRemoveClickListener: OnItemRemoveClickListener
    private lateinit var fragmentManager: FragmentManager


    interface OnItemClickListener {
        fun onItemClick(walk: WalkModel)
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

    fun setFragmentManager(fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager
    }

    private fun removeWalk(position: Int) {
        if (walks.isEmpty() || position !in 0..walks.size) {
            return
        }

        walks.removeAt(position)
        notifyDataSetChanged()
    }

    private fun showRemoveDialog(position: Int) {
        val actionDialogFragment = ActionDialogFragment()

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun action1(isAction: Boolean) {
                if (isAction) {
                    removeWalk(position)
                    mOnItemRemoveClickListener.onItemRemoveClick()
                }
            }

            override fun action2(isAction: Boolean) {
            }
        })

        val bundle = Bundle()
        bundle.putString("msg", "'OO번째 산책' 을 삭제하시겠어요?")

        actionDialogFragment.arguments = bundle
        actionDialogFragment.show(fragmentManager, null)
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

    inner class WalkViewHolder(val binding: ItemWalkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.walkNthRecordTv.text = walks[position].walkIdx.toString()

            binding.root.setOnClickListener {
                mOnItemClickListener.onItemClick(walks[position])
            }

            binding.walkRemoveTv.setOnClickListener {
                showRemoveDialog(position)
            }
        }
    }
}