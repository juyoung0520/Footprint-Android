package com.footprint.footprint.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.databinding.ItemCourseBinding
import com.footprint.footprint.ui.main.course.CourseListFragment

class CourseListRVAdapter(val context: Context): RecyclerView.Adapter<CourseListRVAdapter.ViewHolder>() {
    private val courseList = arrayListOf<CourseDTO>() /* 수정 */

    inner class ViewHolder(val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: CourseDTO, position: Int) {
            // 타이틀, 인포
            binding.itemCourseTitleTv.text = course.courseName
            binding.itemCourseInfoTv.text = "${course.courseDist}km, 약 ${course.courseTime}분"

            // 이미지
            Glide.with(context)
                .load(course.courseImg)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .into(binding.itemCourseImageIv)

            // 사람 수, 찜 수 /* 수정 */

            // tag RV
            val tagRVAdapter = CourseTagRVAdapter(course.courseTags)
            binding.itemCourseTagRv.adapter = tagRVAdapter

            // 찜하기 버튼 관련
            binding.itemCourseLikeIv.isSelected = course.userCourseLike

            binding.itemCourseLikeIv.setOnClickListener {
                // courseIDX 가지고 찜하기 버튼 API 호출
                myCourseClickListener.wishCourse(course.courseIdx)
                binding.itemCourseLikeIv.isSelected = !binding.itemCourseLikeIv.isSelected
            }
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
        fun wishCourse(courseIdx: String)
    }

    private lateinit var myCourseClickListener: CourseClickListener

    fun setMyClickListener(myCourseClickListener: CourseClickListener){
        this.myCourseClickListener = myCourseClickListener
    }
}