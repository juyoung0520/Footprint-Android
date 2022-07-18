package com.footprint.footprint.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.DayWalkDTO
import com.footprint.footprint.data.dto.UserDateWalkDTO
import com.footprint.footprint.databinding.ItemWalkBinding

class WalkRVAdapter(val context: Context) : RecyclerView.Adapter<WalkRVAdapter.WalkViewHolder>() {
    private val walks = arrayListOf<DayWalkDTO>()
    private var currentTag: String ?= null

    private lateinit var mOnItemClickListener: OnItemClickListener
    private lateinit var mOnItemRemoveClickListener: OnItemRemoveClickListener

    interface OnItemClickListener {
        fun onItemClick(walk: UserDateWalkDTO)
    }

    interface OnItemRemoveClickListener {
        fun onItemRemoveClick(walkIdx: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setWalks(walks: List<DayWalkDTO>) {
        this.walks.clear()
        this.walks.addAll(walks)

        notifyDataSetChanged()
    }

    fun setCurrentTag(tag: String) {
        currentTag = tag
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    fun setOnItemRemoveClickListener(listener: OnItemRemoveClickListener) {
        mOnItemRemoveClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeWalk(position: Int) {
        if (walks.isEmpty() || position !in 0..walks.size) {
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

    inner class WalkViewHolder(val binding: ItemWalkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val walk = walks[position].walk
            binding.walkNthRecordTv.text = String.format("%d번째 산책", walk.walkIdx)

            binding.root.setOnClickListener {
                mOnItemClickListener.onItemClick(walk)
            }

            // 검색 결과 화면이면
            if (currentTag != null) {
                binding.walkRemoveTv.visibility = View.GONE
            }

            binding.walkRemoveTv.setOnClickListener {
                mOnItemRemoveClickListener.onItemRemoveClick(walks[position].walk.walkIdx)
            }

            binding.walkTimeTv.text = String.format("%s~%s", walk.startTime, walk.endTime)

            Glide.with(context).load(walk.pathImageUrl).into(binding.walkPathIv)

            val hashtag = walks[position].hashtag
            for (idx in hashtag.indices) {
                when(idx + 1) {
                    1 -> initTag(binding.walkTag1Tv, hashtag[idx])
                    2 -> initTag(binding.walkTag2Tv, hashtag[idx])
                    3 -> initTag(binding.walkTag3Tv, hashtag[idx])
                    4 -> initTag(binding.walkTag4Tv, hashtag[idx])
                    5 -> initTag(binding.walkTag5Tv, hashtag[idx])
                }
            }
        }

        private fun initTag(textView: TextView, tag: String) {
            if (currentTag != null && currentTag == tag) {
                textView.background = getDrawable(context, R.drawable.bg_primary55_round_square)
            }

            textView.visibility = View.VISIBLE
            textView.text = tag
        }
    }
}

