package com.footprint.footprint.ui.adapter

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.footprint.footprint.R
import com.footprint.footprint.data.model.FootprintModel
import com.footprint.footprint.data.model.FootprintsModel
import com.footprint.footprint.databinding.ItemFootprintBinding
import com.volokh.danylo.hashtaghelper.HashTagHelper
import me.relex.circleindicator.CircleIndicator3
import java.util.regex.Matcher
import java.util.regex.Pattern

class FootprintRVAdapter(private val activityClass: String) : RecyclerView.Adapter<FootprintRVAdapter.PostViewHolder>() {
    interface MyItemClickListener {
        fun showDeleteDialog(position: Int)
        fun addFootprint(position: Int)
        fun updateFootprint(position: Int, footprint: FootprintModel)
    }

    private val footprints: ArrayList<FootprintModel> = arrayListOf()
    private val footprintIcList: ArrayList<Int> = arrayListOf(
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

    private lateinit var binding: ItemFootprintBinding
    private lateinit var myItemClickListener: MyItemClickListener
    private lateinit var mTextHashTagHelper: HashTagHelper

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FootprintRVAdapter.PostViewHolder {
        binding = ItemFootprintBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        mTextHashTagHelper = HashTagHelper.Creator.create(
            ContextCompat.getColor(parent.context, R.color.primary),
            null
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FootprintRVAdapter.PostViewHolder, position: Int) {
        //첫 번째 기록에서만 맨 위에 동그라미 뷰 보여주기
        if (position == 0)
            holder.postStartIv.visibility = View.VISIBLE
        else
            holder.postStartIv.visibility = View.INVISIBLE

        //기록 시간
        holder.postTimeTv.text = footprints[position].time

        //기록 삭제 버튼 클릭 리스너 -> 다이얼로그 띄우기
        holder.deleteTv.setOnClickListener {
            myItemClickListener.showDeleteDialog(position)
        }

        //기록 편집 텍스트뷰 클릭 리스너
        holder.editTv.setOnClickListener {
            myItemClickListener.updateFootprint(position, footprints[position])
        }

        holder.footPrintIv.setImageResource(footprintIcList[position])

        //이미지가 있으면 뷰페이저 연결, 없으면 뷰페이저 연결 안함.
        if (footprints[position].photos.size == 0) {
            holder.photoVp.visibility = View.GONE
            holder.photoIndicator.visibility = View.GONE
        } else {
            val photoRVAdapter = PhotoRVAdapter(1)
            photoRVAdapter.addImgList(footprints[position].photos)
            holder.photoVp.adapter = photoRVAdapter
            holder.photoVp.visibility = View.VISIBLE

            holder.photoIndicator.setViewPager(holder.photoVp)
            holder.photoIndicator.visibility = View.VISIBLE
        }

        //발자국 내용 해시태그 폰트 색상 바꾸기
        var hashTagCnt = 0
        val hashtagInContent = SpannableString(footprints[position].content)
        val matcher: Matcher = Pattern.compile("#([A-Za-z0-9_-]+)").matcher(hashtagInContent)
        while (matcher.find() && hashTagCnt < 5) {
            hashTagCnt++    //5개까지만

            hashtagInContent.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(binding.root.context, R.color.primary)),
                matcher.start(),
                matcher.end(),
                0
            )
        }
        holder.contentTv.text = hashtagInContent

        //기록 내용 더보기, 간략히 보기
        holder.contentTv.post(Runnable {
            val lineCnt: Int = holder.contentTv.lineCount

            if (footprints[position].photos.size == 0 && lineCnt > 3) {  //이미지가 없고, 기록 내용이 3줄보다 더 길 때
                holder.contentTv.maxLines = 3
                holder.viewMoreTv.visibility = View.VISIBLE
            } else if (footprints[position].photos.size > 0 && lineCnt > 2) {    //이미지가 있고, 기록 내용이 2줄보다 더 길 때
                holder.contentTv.maxLines = 2
                holder.viewMoreTv.visibility = View.VISIBLE
            } else {    //이외 상황
                holder.viewMoreTv.visibility = View.INVISIBLE
            }
        })

        //기록 내용 더보기, 간략히 보기 클릭 리스너
        holder.viewMoreTv.setOnClickListener {
            if (holder.viewMoreTv.text.toString() == "더보기") {
                holder.contentTv.maxLines = Int.MAX_VALUE
                holder.viewMoreTv.text = "간략히 보기"
            } else {
                holder.viewMoreTv.text = "더보기"

                if (footprints[position].photos.size == 0)
                    holder.contentTv.maxLines = 3
                else
                    holder.contentTv.maxLines = 2
            }
        }

        if (activityClass=="WalkAfterActivity") {  //산책 종료 후 산책 정보 확인 화면일 때 -> 발자국 추가 가능
            holder.plusTv.visibility = View.VISIBLE
            holder.plusLine.visibility = View.VISIBLE

            //기록 추가 텍스트뷰 클릭 리스너
            holder.plusTv.setOnClickListener {
                myItemClickListener.addFootprint(position)
            }
        } else {    //산책 정보 상세 화면일 때 -> 발자국 추가 불가능
            holder.plusTv.visibility = View.INVISIBLE
            holder.plusLine.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int = footprints.size

    fun setData(footprints: FootprintsModel) {
        this.footprints.clear()
        this.footprints.addAll(footprints.footprints)
        notifyDataSetChanged()
    }

    fun removeData(position: Int) {
        this.footprints.removeAt(position)
        notifyDataSetChanged()
    }

    fun addData(footprint: FootprintModel, position: Int) {
        Log.d("PostRVAdapter", "addData before -> ${this.footprints}")
        if (this.footprints.size == position)
            this.footprints.add(footprint)
        else
            this.footprints.add(position, footprint)

        notifyDataSetChanged()
        Log.d("PostRVAdapter", "addData after -> ${this.footprints}")
    }

    fun updateData(footprint: FootprintModel, position: Int) {
        footprint.isUpdate = false
        footprints[position] = footprint
        notifyDataSetChanged()
    }

    fun setMyItemClickListener(myItemClickListener: MyItemClickListener) {
        this.myItemClickListener = myItemClickListener
    }

    inner class PostViewHolder(itemView: ItemFootprintBinding) : RecyclerView.ViewHolder(itemView.root) {
        val postStartIv: ImageView = itemView.itemFootprintPostStartIv
        val postTimeTv: TextView = itemView.itemFootprintPostTimeTv
        val photoVp: ViewPager2 = itemView.itemFootprintPhotoVp
        val photoIndicator: CircleIndicator3 = itemView.itemFootprintPhotoIndicator
        val contentTv: TextView = itemView.itemFootprintContentTv
        val deleteTv: TextView = itemView.itemFootprintDeleteTv
        val viewMoreTv: TextView = itemView.itemFootprintViewMoreTv
        val footPrintIv: ImageView = itemView.itemFootprintFootprintIv
        val plusTv: TextView = itemView.itemFootprintPlusTv
        val plusLine: View = itemView.itemFootprintPlusLineView
        val editTv: TextView = itemView.itemFootprintEditTv
    }
}