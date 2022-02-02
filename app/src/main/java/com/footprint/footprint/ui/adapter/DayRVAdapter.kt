package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemDayBinding

class DayRVAdapter(private val size: Int) : RecyclerView.Adapter<DayRVAdapter.DayViewHolder>() {

    interface MyItemClickListener {
        fun saveDay(day: Int)
        fun removeDay(day: Int)
    }

    private lateinit var myItemClickListener: MyItemClickListener
    private lateinit var binding: ItemDayBinding

    private var isEnabled: Boolean = true

    private val day = arrayListOf<String>("월", "화", "수", "목", "금", "토", "일")
    private val userGoalDay: ArrayList<Int> = arrayListOf()  //사용자 목표 요일(이번달 목표 화면에서 사용)

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

        //아이템뷰 활성화/비활성화 설정 -> 이번달 목표 조회일 경우 비활성화
        holder.day.isEnabled = isEnabled

        holder.day.text = day[position]   //텍스트 바인딩(월화수목금토일)

        //이번달 목표 화면의 경우 사용자가 선택한 목표 요일은 선택된 상태로 보여야 한다.
        holder.day.isSelected = userGoalDay.isNotEmpty() && userGoalDay.contains(position+1)

        //클릭 리스너 -> 클릭됨: 하늘색, 클릭 안됨: 하얀색
        holder.day.setOnClickListener {
            if (it.isSelected) {
                it.isSelected = false
                myItemClickListener.removeDay(position+1)
            } else {
                it.isSelected = true
                myItemClickListener.saveDay(position+1)
            }
        }
    }

    override fun getItemCount(): Int = day.size

    fun setMyItemClickListener(myItemClickListener: MyItemClickListener) {
        this.myItemClickListener = myItemClickListener
    }

    fun setEnabled(isEnabled: Boolean) {
        this.isEnabled = isEnabled
    }

    fun setUserGoalDay(userGoalDay: ArrayList<Int>) {
        this.userGoalDay.addAll(userGoalDay)
    }

    inner class DayViewHolder(itemView: ItemDayBinding) : RecyclerView.ViewHolder(itemView.root) {
        val day: TextView = itemView.itemDayDayTv
    }
}