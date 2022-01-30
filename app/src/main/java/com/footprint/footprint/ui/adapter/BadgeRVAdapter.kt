package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.R
import com.footprint.footprint.data.remote.badge.Badge
import com.footprint.footprint.databinding.ItemBadgeBinding

class BadgeRVAdapter(private var representativeBadge: Badge, private val size: Int) :
    RecyclerView.Adapter<BadgeRVAdapter.BadgeViewHolder>() {

    interface MyItemClickListener {
        fun changeRepresentativeBadge(badge: Badge)
    }

    private lateinit var binding: ItemBadgeBinding
    private lateinit var myItemClickListener: MyItemClickListener

    private val badges: ArrayList<Badge> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BadgeRVAdapter.BadgeViewHolder {
        binding = ItemBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeRVAdapter.BadgeViewHolder, position: Int) {
        //아이템 width, height 설정
        val params = holder.badgeIv.layoutParams
        params.width = size
        params.height = size
        holder.badgeIv.layoutParams = params

        val badge = badges.find { it.order==position }
        if (badge==null) {
            //뱃지 이미지
            holder.badgeIv.setImageResource(R.drawable.ic_no_badge)
            //뱃지 이름 -> 얻은 뱃지는 이름을 보여주고, 얻지  못한 뱃지는 이름을 보여주지 않는다.
            holder.badgeName.visibility = View.INVISIBLE
            //뱃지 클릭 리스너 비활성화
            holder.root.isEnabled = false
        } else {
            //뱃지 이미지
            holder.badgeIv.setImageResource(badge.img)
            //뱃지 이름 -> 얻은 뱃지는 이름을 보여주고, 얻지  못한 뱃지는 이름을 보여주지 않는다.
            holder.badgeName.apply {
                text = badge.name
                visibility = View.VISIBLE
            }
            //뱃지 클릭 리스너 활성화
            holder.root.isEnabled = true
        }

        //대표뱃지에는 대표뱃지 뷰를 보여주고, 그 외 뱃지는 보여주지 않는다.
        if (representativeBadge.order == position)
            holder.representativeBadge.visibility = View.VISIBLE
        else
            holder.representativeBadge.visibility = View.GONE

        //대표 뱃지 변경
        holder.root.setOnClickListener {
            myItemClickListener.changeRepresentativeBadge(badges.find { it.order==position }!!)
        }
    }

    override fun getItemCount(): Int = 19

    fun setData(badges: List<Badge>) {
        this.badges.addAll(badges)
    }

    fun setMyItemClickListener(myItemClickListener: MyItemClickListener) {
        this.myItemClickListener = myItemClickListener
    }

    fun changeRepresentativeBadge(representativeBadge: Badge) {
        this.representativeBadge = representativeBadge
        notifyDataSetChanged()
    }

    fun addBadge(badge: Badge) {
        this.badges.add(badge)
        notifyDataSetChanged()
    }

    inner class BadgeViewHolder(binding: ItemBadgeBinding) : RecyclerView.ViewHolder(binding.root) {
        val root: ConstraintLayout = binding.root
        val badgeIv: ImageView = binding.itemBadgeBadgeIv
        val badgeName: TextView = binding.itemBadgeBadgeNameTv
        val representativeBadge: View = binding.itemBadgeRepresentativeBadgeView
    }
}