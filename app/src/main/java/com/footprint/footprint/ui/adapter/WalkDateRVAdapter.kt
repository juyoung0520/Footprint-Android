package com.footprint.footprint.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.data.remote.walk.WalkDateResult
import com.footprint.footprint.data.remote.walk.WalkService
import com.footprint.footprint.databinding.ItemWalkDateBinding
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.main.calendar.CalendarView
import com.footprint.footprint.ui.main.calendar.SearchResultView

class WalkDateRVAdapter(val context: Context) : RecyclerView.Adapter<WalkDateRVAdapter.WalkDateViewHolder>() {
    private val walkDates = arrayListOf<WalkDateResult>()
    private lateinit var currentTag: String

    private lateinit var onWalkDateRemoveListener: OnWalkDateRemoveListener

    private lateinit var onWalkClickListener: WalkRVAdapter.OnItemClickListener
    private lateinit var fragmentManager: FragmentManager

    interface OnWalkDateRemoveListener {
        fun onWalkDateRemove()
    }

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

    fun setWalkDateRemoveListener(listener: WalkDateRVAdapter.OnWalkDateRemoveListener) {
        this.onWalkDateRemoveListener = listener
    }

    fun setFragmentManager(fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager
    }

    @SuppressLint("NotifyDataSetChanged")
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

    private fun showRemoveDialog(walkIdx: Int) {
        val actionDialogFragment = ActionDialogFragment()

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun action1(isAction: Boolean) {
                if (isAction) {
                    // remove API
                    WalkService.deleteWalk(context as SearchResultView, walkIdx)
                }
            }

            override fun action2(isAction: Boolean) {
            }
        })

        val bundle = Bundle()
        bundle.putString("msg", "'${walkIdx}번째 산책' 을 삭제하시겠어요?")

        actionDialogFragment.arguments = bundle
        actionDialogFragment.show(fragmentManager, null)
    }

    inner class WalkDateViewHolder(val binding: ItemWalkDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.walkDateDateTv.text = walkDates[position].walkAt

            val adapter = WalkRVAdapter(context)
            adapter.setWalks(walkDates[position].walks)
            adapter.setCurrentTag(currentTag)

            adapter.setOnItemClickListener(onWalkClickListener)
            adapter.setOnItemRemoveListener(object : WalkRVAdapter.OnItemRemoveClickListener {
                override fun onItemRemoveClick(walkIdx: Int) {
                    showRemoveDialog(walkIdx)
//                    if (adapter.itemCount == 0) {
//                        removeWalkDate(position)
//                        onWalkDateRemoveListener.onWalkDateRemove()
//                    }
                }
            })

            binding.walkDateWalksRv.adapter = adapter
        }
    }
}