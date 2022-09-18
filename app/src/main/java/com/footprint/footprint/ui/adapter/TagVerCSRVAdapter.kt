package com.footprint.footprint.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ItemTagVerCsBinding
import com.footprint.footprint.domain.model.CourseHashtagEntity

class TagVerCSRVAdapter(val context: Context): RecyclerView.Adapter<TagVerCSRVAdapter.TagVerCSViewHolder>() {
    interface MyClickListener {
        fun checked(isChecked: Boolean)
    }

    private var checkedTags: MutableList<CourseHashtagEntity> = mutableListOf()
    private var tags: List<CourseHashtagEntity> = listOf()
    private lateinit var myClickListener: MyClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagVerCSRVAdapter.TagVerCSViewHolder {
        val binding = ItemTagVerCsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagVerCSViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagVerCSRVAdapter.TagVerCSViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = tags.size

    fun setData(tags: List<CourseHashtagEntity>, checkedTags: List<CourseHashtagEntity>) {
        this.tags = tags
        this.checkedTags.addAll(checkedTags)

        notifyDataSetChanged()
    }

    fun setMyClickListener(myClickListener: MyClickListener) {
        this.myClickListener = myClickListener
    }

    fun getCheckedTags(): MutableList<CourseHashtagEntity> = checkedTags

    inner class TagVerCSViewHolder(val binding: ItemTagVerCsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tagVerCsTagTv.text = tags[position].hashtag

            if (checkedTags.contains(tags[position])) {
                setCheckedUI()
            } else {
                setUncheckedUI()
            }

            binding.root.setOnClickListener {
                if (binding.tagVerCsClearIv.visibility==View.GONE) {    //클릭
                    checkedTags.add(tags[position])
                    myClickListener.checked(true)
                    setCheckedUI()
                } else {    //클릭 취소
                    checkedTags.remove(tags[position])
                    myClickListener.checked(false)
                    setUncheckedUI()
                }
            }
        }

        private fun setCheckedUI() {
            binding.tagVerCsTagTv.setTextColor(ContextCompat.getColor(context, R.color.white_light))
            binding.tagVerCsClearIv.visibility = View.VISIBLE
            (binding.root as ConstraintLayout).setBackgroundResource(R.drawable.bg_course_share_tag_clicked)
        }

        private fun setUncheckedUI() {
            binding.tagVerCsTagTv.setTextColor(ContextCompat.getColor(context, R.color.primary))
            binding.tagVerCsClearIv.visibility = View.GONE
            (binding.root as ConstraintLayout).setBackgroundResource(R.drawable.bg_course_share_tag_not_clicked)
        }
    }
}