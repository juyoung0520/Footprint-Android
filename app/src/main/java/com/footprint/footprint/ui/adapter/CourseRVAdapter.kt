package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemCourseBinding
import com.footprint.footprint.utils.convertDpToPx

class CourseRVAdapter: RecyclerView.Adapter<CourseRVAdapter.CourseViewHolder>() {
    interface MyClickListener {
        fun onClick(position: Int)
    }

    private lateinit var binding: ItemCourseBinding
    private lateinit var myClickListener: MyClickListener
    private var testList = mutableListOf(1, 2, 3, 4, 5, 6, 7)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CourseRVAdapter.CourseViewHolder {
        binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseRVAdapter.CourseViewHolder, position: Int) {
        if (position==0) {  //첫번째 아이템의 topMargin 을 16dp 로 설정
            holder.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = convertDpToPx(holder.root.context, 16)
            }
        }

        holder.root.setOnClickListener {
            myClickListener.onClick(position)
        }
        holder.rv.adapter = TagVerMyRVAdapter()
        holder.tv.text = "힐링코스${testList[position]}\uD83C\uDF40"
    }

    override fun getItemCount(): Int = testList.size

    fun setMyClickListener(myClickListener: MyClickListener) {
        this.myClickListener = myClickListener
    }

    fun removeData(position: Int) {
        testList.removeAt(position)

        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, testList.size - position)
    }

    inner class CourseViewHolder(binding: ItemCourseBinding): RecyclerView.ViewHolder(binding.root) {
        val root: CardView = binding.root
        val rv: RecyclerView = binding.itemCourseTagRv
        val tv: TextView = binding.itemCourseTitleTv
    }
}