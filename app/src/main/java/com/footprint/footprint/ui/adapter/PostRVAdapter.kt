package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ItemPostBinding
import com.footprint.footprint.model.PostModel
import me.relex.circleindicator.CircleIndicator3

class PostRVAdapter() : RecyclerView.Adapter<PostRVAdapter.PostViewHolder>() {
    interface MyItemClickListener {
        fun showDeleteDialog()
    }

    private val posts: ArrayList<PostModel> = arrayListOf()
    private val footprints: ArrayList<Int> = arrayListOf(
        R.drawable.ic_foot_print1,
        R.drawable.ic_foot_print2,
        R.drawable.ic_foot_print3,
        R.drawable.ic_foot_print4,
        R.drawable.ic_foot_print5,
        R.drawable.ic_foot_print6,
        R.drawable.ic_foot_print7,
        R.drawable.ic_foot_print8,
        R.drawable.ic_foot_print9
    )

    private lateinit var binding: ItemPostBinding
    private lateinit var myItemClickListener: MyItemClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostRVAdapter.PostViewHolder {
        binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostRVAdapter.PostViewHolder, position: Int) {
        //첫 번째 기록에서만 맨 위에 동그라미 뷰 보여주기
        if (position == 0)
            holder.postStartIv.visibility = View.VISIBLE
        else
            holder.postStartIv.visibility = View.INVISIBLE

        //기록 시간
        holder.postTimeTv.text = posts[position].time

        //기록 삭제 버튼 클릭 리스너 -> 다이얼로그 띄우기
        holder.deleteTv.setOnClickListener {
            myItemClickListener.showDeleteDialog()
        }

        holder.footPrintIv.setImageResource(footprints[position])

        //이미지가 있으면 뷰페이저 연결, 없으면 뷰페이저 연결 안함.
        if (posts[position].photos.size == 0) {
            holder.photoVp.visibility = View.GONE
            holder.photoIndicator.visibility = View.GONE
        } else {
            val photoRVAdapter = PhotoRVAdapter(1)
            photoRVAdapter.addImgIntData(posts[position].photos)
            holder.photoVp.adapter = photoRVAdapter
            holder.photoVp.visibility = View.VISIBLE

            holder.photoIndicator.setViewPager(holder.photoVp)
            holder.photoIndicator.visibility = View.VISIBLE
        }

        holder.contentTv.text = posts[position].content //기록 내용

        //기록 내용 더보기, 간략히 보기
        holder.contentTv.post(Runnable {
            val lineCnt: Int = holder.contentTv.lineCount

            if (posts[position].photos.size == 0 && lineCnt > 3) {  //이미지가 없고, 기록 내용이 3줄보다 더 길 때
                holder.contentTv.maxLines = 3
                holder.viewMoreTv.visibility = View.VISIBLE
            } else if (posts[position].photos.size > 0 && lineCnt > 2) {    //이미지가 있고, 기록 내용이 2줄보다 더 길 때
                holder.contentTv.maxLines = 2
                holder.viewMoreTv.visibility = View.VISIBLE
            } else {    //이외 상황
                holder.viewMoreTv.visibility = View.GONE
            }
        })

        //기록 내용 더보기, 간략히 보기 클릭 리스너
        holder.viewMoreTv.setOnClickListener {
            if (holder.viewMoreTv.text.toString() == "더보기") {
                holder.contentTv.maxLines = Int.MAX_VALUE
                holder.viewMoreTv.text = "간략히 보기"
            } else {
                holder.viewMoreTv.text = "더보기"

                if (posts[position].photos.size == 0)
                    holder.contentTv.maxLines = 3
                else
                    holder.contentTv.maxLines = 2
            }
        }
    }

    override fun getItemCount(): Int = posts.size

    fun setData(posts: ArrayList<PostModel>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    fun setMyItemClickListener(myItemClickListener: MyItemClickListener) {
        this.myItemClickListener = myItemClickListener
    }

    inner class PostViewHolder(itemView: ItemPostBinding) : RecyclerView.ViewHolder(itemView.root) {
        val postStartIv: ImageView = itemView.itemPostPostStartIv
        val postTimeTv: TextView = itemView.itemPostPostTimeTv
        val photoVp: ViewPager2 = itemView.itemPostPhotoVp
        val photoIndicator: CircleIndicator3 = itemView.itemPostPhotoIndicator
        val contentTv: TextView = itemView.itemPostContentTv
        val deleteTv: TextView = itemView.itemPostDeleteTv
        val viewMoreTv: TextView = itemView.itemPostViewMoreTv
        val footPrintIv: ImageView = itemView.itemPostFootprintIv
    }
}