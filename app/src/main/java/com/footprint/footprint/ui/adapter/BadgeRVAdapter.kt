package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ItemBadgeBinding

class BadgeRVAdapter(private var representativeBadge: Int, private val size: Int) :
    RecyclerView.Adapter<BadgeRVAdapter.BadgeViewHolder>() {

    interface MyItemClickListener {
        fun changeRepresentativeBadge(badge: Int, position: Int)
    }

    private lateinit var binding: ItemBadgeBinding
    private lateinit var myItemClickListener: MyItemClickListener

    private val badges: ArrayList<Int> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BadgeRVAdapter.BadgeViewHolder {
        binding = ItemBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeRVAdapter.BadgeViewHolder, position: Int) {
        //뱃지 이미지
        val params = holder.badgeIv.layoutParams
        params.width = size
        params.height = size
        holder.badgeIv.layoutParams = params
        holder.badgeIv.setImageResource(badges[position])

        //뱃지 이름 -> 얻은 뱃지는 이름을 보여주고, 얻지  못한 뱃지는 이름을 보여주지 않는다.
        if (badges[position] == R.drawable.ic_no_badge)
            holder.badgeName.visibility = View.INVISIBLE
        else
            holder.badgeName.visibility = View.VISIBLE

        //대표뱃지에는 대표뱃지 뷰를 보여주고, 그 외 뱃지는 보여주지 않는다.
        if (representativeBadge == position)
            holder.representativeBadge.visibility = View.VISIBLE
        else
            holder.representativeBadge.visibility = View.GONE

        //대표 뱃지 변경
        holder.root.setOnClickListener {
            myItemClickListener.changeRepresentativeBadge(badges[position], position)
        }
    }

    override fun getItemCount(): Int = 19

    fun setData(badges: ArrayList<Int>) {
        this.badges.addAll(badges)
    }

    fun setMyItemClickListener(myItemClickListener: MyItemClickListener) {
        this.myItemClickListener = myItemClickListener
    }

    fun changeRepresentativeBadge(position: Int) {
        representativeBadge = position

        notifyDataSetChanged()
    }

    inner class BadgeViewHolder(binding: ItemBadgeBinding) : RecyclerView.ViewHolder(binding.root) {
        val root: ConstraintLayout = binding.root
        val badgeIv: ImageView = binding.itemBadgeBadgeIv
        val badgeName: TextView = binding.itemBadgeBadgeNameTv
        val representativeBadge: View = binding.itemBadgeRepresentativeBadgeView
    }
}