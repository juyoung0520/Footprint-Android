package com.footprint.footprint.ui.main.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ItemHomeMonthBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeMonthRVAdapter(val calLayout: LinearLayout, val date: Date): RecyclerView.Adapter<HomeMonthRVAdapter.ViewHolder>() {

    var dataList: ArrayList<Int> = arrayListOf()

    //커스텀 캘린더 클래스를 이용하여 날짜 세팅
    var homemonthCalendar: HomeMonthCalendar = HomeMonthCalendar(date)
    init{
        homemonthCalendar.initBaseCalendar() //init 후
        dataList = homemonthCalendar.dateList //datelist -> datalist에 전달
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): HomeMonthRVAdapter.ViewHolder {
        val binding: ItemHomeMonthBinding = ItemHomeMonthBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeMonthRVAdapter.ViewHolder, position: Int) {
        // 높이 지정
        val height = calLayout.height/6 //왜 6인지?
        holder.itemView.layoutParams.height = height
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(val binding: ItemHomeMonthBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: Int, position: Int){
            //첫날, 마지막날 세팅
            val firstDateIndex = homemonthCalendar.prevTail
            val lastDateIndex = dataList.size - homemonthCalendar.nextHead - 1

            binding.itemHomeMonthDayTv.text = data.toString()
            //binding.itemHomeMonthDayTv.text = dataList[position].toString()

            /*스타일*/
            // 오늘 날짜 bold체로
            var dateString: String = SimpleDateFormat("dd", Locale.KOREA).format(date)
            var dateInt = dateString.toInt()
            if (dataList[position] == dateInt) {
                binding.itemHomeMonthDayTv.setTextAppearance(R.style.tv_body_b_12)
            }

            // 현재 월의 1일 이전, 현재 월의 마지막일 이후 값의 텍스트를 회색처리
            if (position < firstDateIndex || position > lastDateIndex) {
                binding.itemHomeMonthDayTv.setTextColor(Color.parseColor("#E1E2E1"))
                binding.itemHomeMonthDayTv.background = null
            }
            binding.itemHomeMonthDayPb.progress = 80
        }
    }
}