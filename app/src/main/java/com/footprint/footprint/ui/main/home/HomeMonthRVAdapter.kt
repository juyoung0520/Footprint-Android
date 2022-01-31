package com.footprint.footprint.ui.main.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ItemHomeMonthBinding
import java.util.*

class HomeMonthRVAdapter(private val date: Date, private val widthPx: Int, private val vpAreaPx: Int, private val itemMaxPx: Int) :
    RecyclerView.Adapter<HomeMonthRVAdapter.ViewHolder>() {

    private var dataList: ArrayList<Int> = arrayListOf()
    private var weeks: Int = 0

    //커스텀 캘린더 클래스를 이용하여 날짜 세팅
    private var homemonthCalendar: HomeMonthCalendar = HomeMonthCalendar(date)
    init{
        homemonthCalendar.initBaseCalendar() //init 후
        dataList = homemonthCalendar.dateList //datelist -> datalist에 전달
        weeks = homemonthCalendar.weeks
    }


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

        var itemWidthPx = widthPx / 7 // Device Width(dp) - 양 옆 마진(30*2) / 7(일~토)
        var itemHeightPx = vpAreaPx / weeks
        if(itemWidthPx <= itemMaxPx) itemWidthPx = itemMaxPx + 2
        if(itemHeightPx <= itemMaxPx) itemHeightPx = itemMaxPx + 2

        params.width = itemWidthPx
        params.height = itemHeightPx

        holder.itemView.layoutParams = params
        holder.itemView.requestApplyInsets()

        holder.bind(dataList[position], position)
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(val binding: ItemHomeMonthBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Int, position: Int) {
            //첫날, 마지막날 세팅
            val firstDateIndex = homemonthCalendar.prevTail
            val lastDateIndex = dataList.size - homemonthCalendar.nextHead - 1

            binding.itemHomeMonthDayTv.text = data.toString()

            /*스타일*/
            // 오늘 날짜 bold체로
            val  dateInt = date.date
            if ((position in firstDateIndex .. lastDateIndex) && dataList[position] == dateInt) {
                binding.itemHomeMonthDayTv.setTextAppearance(R.style.tv_headline_eb_12)
            }

            //프로그레스바 표현
            if (position % 10 == 0) {
                binding.itemHomeMonthDayPb.visibility = View.VISIBLE
                binding.itemHomeMonthDayPb.progress = 80
            }

            // 현재 월의 1일 이전, 현재 월의 마지막일 이후 값의 텍스트를 회색처리
            if (position < firstDateIndex || position > lastDateIndex) {
                binding.itemHomeMonthDayTv.setTextColor(Color.parseColor("#E1E2E1"))
                binding.itemHomeMonthDayTv.background = null
                binding.itemHomeMonthDayPb.visibility = View.GONE
            }

        }
    }

}