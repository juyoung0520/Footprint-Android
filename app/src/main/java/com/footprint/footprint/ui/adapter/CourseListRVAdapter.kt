package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.ItemCourseBinding

class CourseListRVAdapter: RecyclerView.Adapter<CourseListRVAdapter.ViewHolder>() {
    private val courseList = arrayListOf<CourseDTO>() /* 수정 */

    inner class ViewHolder(val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: CourseDTO, position: Int) {
            // 타이틀, 인포

            // 사람 수, 찜 수

            // tag RV
            val tagRVAdapter = CourseTagRVAdapter()
            binding.itemCourseTagRv.adapter = tagRVAdapter

            // 찜하기 버튼 관련
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(courseList[position], position)
        holder.binding.root.setOnClickListener{
            myCourseClickListener.onClick(courseList[position])
        }
    }

    override fun getItemCount(): Int = courseList.size

    /* 아이템 관리 */
    fun addAll(list: ArrayList<CourseDTO>){
        courseList.clear()
        courseList.addAll(list)
        notifyDataSetChanged()
    }

    /* 클릭 이벤트 관리 */
    interface CourseClickListener{
        fun onClick(course: CourseDTO)
    }

    private lateinit var myCourseClickListener: CourseClickListener

    fun setMyClickListener(myCourseClickListener: CourseClickListener){
        this.myCourseClickListener = myCourseClickListener
    }
}