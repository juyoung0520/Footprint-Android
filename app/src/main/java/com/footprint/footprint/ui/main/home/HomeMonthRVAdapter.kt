package com.footprint.footprint.ui.main.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ItemHomeMonthBinding
import java.util.*

class HomeMonthRVAdapter(private val dataList: ArrayList<Int>, private val today: Int, private val firstDateIdx: Int, private val lastDateIdx: Int, private val margin:Int) :
    RecyclerView.Adapter<HomeMonthRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): HomeMonthRVAdapter.ViewHolder {
        val binding: ItemHomeMonthBinding =
            ItemHomeMonthBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams

        if(position < 7){
            params.topMargin = margin/6
        }else{
            params.topMargin = margin/4
        }

        holder.itemView.layoutParams = params
        holder.itemView.requestApplyInsets()

        holder.bind(dataList[position], position)
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(val binding: ItemHomeMonthBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Int, position: Int) {
            binding.itemHomeMonthDayTv.text = data.toString()

            /*스타일*/
            // 오늘 날짜 bold체로
            var dateInt = today
            if (dataList[position] == dateInt) {
                binding.itemHomeMonthDayTv.setTextAppearance(R.style.tv_headline_eb_12)
            }

            //프로그레스바 표현
            if (position % 10 == 0) {
                binding.itemHomeMonthDayPb.visibility = View.VISIBLE
                binding.itemHomeMonthDayPb.progress = 80
            }

            // 현재 월의 1일 이전, 현재 월의 마지막일 이후 값의 텍스트를 회색처리
            if (position < firstDateIdx || position > lastDateIdx) {
                binding.itemHomeMonthDayTv.setTextColor(Color.parseColor("#E1E2E1"))
                binding.itemHomeMonthDayTv.background = null
                binding.itemHomeMonthDayPb.visibility = View.GONE
            }

        }
    }

}