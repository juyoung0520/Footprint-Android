package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.data.dto.NoticeInfoDto
import com.footprint.footprint.databinding.ItemNoticeBinding
import kotlin.collections.ArrayList

class NoticeRVAdapter() : RecyclerView.Adapter<NoticeRVAdapter.ViewHolder>() {
     private val noticeList = ArrayList<NoticeInfoDto>()

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

    fun updateNoticeList(notices: ArrayList<NoticeInfoDto>){
        noticeList.clear()
        noticeList.addAll(notices)

        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: NoticeInfoDto) {
            binding.itemNoticeTitleTv.text = notice.title

            // new 처리
            if(notice.isNewNotice)
                binding.noticeNewTv.visibility = View.VISIBLE
            else
                binding.noticeNewTv.visibility = View.GONE

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