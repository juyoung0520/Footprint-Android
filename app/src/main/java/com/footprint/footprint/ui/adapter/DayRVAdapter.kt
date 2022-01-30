package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ItemDayBinding

class DayRVAdapter(private val size: Int) : RecyclerView.Adapter<DayRVAdapter.DayViewHolder>() {

    interface MyItemClickListener {
        fun saveDay(day: Int)
        fun removeDay(day: Int)
    }

    private lateinit var myItemClickListener: MyItemClickListener

    private val day = arrayListOf<Int>(
        R.string.title_mon_kr,
        R.string.title_tue_kr,
        R.string.title_wed_kr,
        R.string.title_thu_kr,
        R.string.title_fri_kr,
        R.string.title_sat_kr,
        R.string.title_sun_kr
    )

    private lateinit var binding: ItemDayBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayRVAdapter.DayViewHolder {
        binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayRVAdapter.DayViewHolder, position: Int) {
        //아이템 뷰 사이즈 변경
        val params = holder.day.layoutParams
        params.width = size
        params.height = size
        holder.day.layoutParams = params

        holder.day.setText(day[position])   //텍스트 바인딩(월화수목금토일)

        //클릭 리스너 -> 클릭됨: 하늘색, 클릭 안됨: 하얀색
        holder.day.setOnClickListener {
            if (it.isSelected) {
                it.isSelected = false
                myItemClickListener.removeDay(day[position])
            } else {
                it.isSelected = true
                myItemClickListener.saveDay(day[position])
            }
        }
    }

    override fun getItemCount(): Int = day.size

    fun setMyItemClickListener(myItemClickListener: MyItemClickListener) {
        this.myItemClickListener = myItemClickListener
    }

    inner class DayViewHolder(itemView: ItemDayBinding) : RecyclerView.ViewHolder(itemView.root) {
        val day: TextView = itemView.itemDayDayTv
    }
}