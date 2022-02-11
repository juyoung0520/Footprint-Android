package com.footprint.footprint.ui.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.R
import com.footprint.footprint.data.remote.achieve.TMonthDayRateRes
import com.footprint.footprint.databinding.ItemHomeMonthBinding
import com.footprint.footprint.ui.main.home.HomeMonthCalendar
import java.util.*
import kotlin.collections.ArrayList

class HomeMonthRVAdapter(
    private val date: Date,
    private var userDatas: ArrayList<TMonthDayRateRes>?,
    private val widthPx: Int,
    private val vpAreaPx: Int,
    private val itemMaxPx: Int
) :
    RecyclerView.Adapter<HomeMonthRVAdapter.ViewHolder>() {

    private var dataList: ArrayList<Int> = arrayListOf()
    private var weeks: Int = 0

    //커스텀 캘린더 클래스를 이용하여 날짜 세팅
    private var homemonthCalendar: HomeMonthCalendar = HomeMonthCalendar(date)

    init {
        homemonthCalendar.initBaseCalendar() //init 후
        dataList = homemonthCalendar.dateList //datelist -> datalist에 전달
        weeks = homemonthCalendar.weeks
    }


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemHomeMonthBinding =
            ItemHomeMonthBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams

        var itemWidthPx = widthPx / 7 // Device Width(dp) - 양 옆 마진(30*2) / 7(일~토)
        var itemHeightPx = vpAreaPx / weeks
        if (itemWidthPx <= itemMaxPx) itemWidthPx = itemMaxPx + 2
        if (itemHeightPx <= itemMaxPx) itemHeightPx = itemMaxPx + 2

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

            //날짜 텍스트 세팅
            binding.itemHomeMonthDayTv.text = data.toString()

            /*스타일*/
            // 오늘 날짜 bold체로
            val dateInt = date.date
            Log.d("CALENDAR", "date: $date dateInt: $dateInt")
            if ((position in firstDateIndex..lastDateIndex) && data == dateInt) {
                binding.itemHomeMonthDayTv.setTextAppearance(R.style.tv_headline_eb_12)
            }else{
                binding.itemHomeMonthDayTv.setTextAppearance(R.style.tv_caption_12)
            }

            // 현재 월의 1일 이전, 현재 월의 마지막일 이후 값의 텍스트를 회색처리
            if (position < firstDateIndex || position > lastDateIndex) {
                binding.itemHomeMonthDayTv.setTextColor(Color.parseColor("#E1E2E1"))
                binding.itemHomeMonthDayTv.background = null
                binding.itemHomeMonthDayPb.visibility = View.GONE
            }else{
                binding.itemHomeMonthDayTv.setTextColor(Color.parseColor("#241F20"))
            }

            /*달성율 반영*/
            if (userDatas != null && position in firstDateIndex..lastDateIndex) {
                for (userData in userDatas!!) {
                    if (userData.day == data) {
                        //프로그레스 바 visibility -> VISIBLE
                        binding.itemHomeMonthDayPb.visibility = View.VISIBLE

                        //달성율 반영
                        val rate = userData.rate.toInt()
                        val color = if(rate == 100) "#4FB8E7" else "#FFC01D"
                        binding.itemHomeMonthDayPb.progress = rate
                        binding.itemHomeMonthDayPb.setProgressStartColor(Color.parseColor(color))
                        binding.itemHomeMonthDayPb.setProgressEndColor(Color.parseColor(color))
                    }
                }
            }


        }
    }


}