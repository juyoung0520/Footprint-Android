package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.data.dto.NoticeInfo
import com.footprint.footprint.databinding.ItemNoticeBinding
import kotlin.collections.ArrayList

class NoticeRVAdapter() : RecyclerView.Adapter<NoticeRVAdapter.ViewHolder>() {
     private val noticeList = ArrayList<NoticeInfo>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NoticeRVAdapter.ViewHolder {
        val binding: ItemNoticeBinding = ItemNoticeBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeRVAdapter.ViewHolder, position: Int) {
        holder.bind(noticeList[position]) // data 수정
        holder.binding.root.setOnClickListener {
            myItemClickListener.showNoticeDetail(noticeList[position].noticeIdx)
        }
    }

    override fun getItemCount(): Int = noticeList.size

    fun updateNoticeList(notices: ArrayList<NoticeInfo>){
        this.noticeList.clear()
        this.noticeList.addAll(notices)

        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: NoticeInfo) {
            binding.itemNoticeTitleTv.text = notice.title

            // new 처리
        }
    }

    /* 클릭 이벤트 관리 */
    interface MyItemClickListener{
        fun showNoticeDetail(idx: Int)
    }

    private lateinit var myItemClickListener: NoticeRVAdapter.MyItemClickListener

    fun setMyItemClickListener(myItemClickListener: NoticeRVAdapter.MyItemClickListener) {
        this.myItemClickListener = myItemClickListener
    }
}