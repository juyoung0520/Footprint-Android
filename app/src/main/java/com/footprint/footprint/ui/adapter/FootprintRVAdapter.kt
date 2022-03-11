package com.footprint.footprint.ui.adapter

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.footprint.footprint.R
import com.footprint.footprint.data.model.FootprintModel
import com.footprint.footprint.data.remote.footprint.Footprint
import com.footprint.footprint.databinding.ItemFootprintBinding
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.convertDpToPx
import com.volokh.danylo.hashtaghelper.HashTagHelper
import me.relex.circleindicator.CircleIndicator3
import java.util.regex.Matcher
import java.util.regex.Pattern

class FootprintRVAdapter() :
    RecyclerView.Adapter<FootprintRVAdapter.PostViewHolder>() {
    interface MyItemClickListener {
        fun addFootprint(position: Int)
        fun updateFootprintVerAfter(position: Int, footprint: FootprintModel)
        fun updateFootprintVerDetail(position: Int, footprint: Footprint)
    }

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

    private var footprintIcIdx: Int = 0
    private var footprintsAfterVer: ArrayList<FootprintModel>? = null
    private var footprintsDetailVer: ArrayList<Footprint>? = null

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
        val lineMargin = convertDpToPx(binding.root.context, 5)

        if (footprintsAfterVer==null && footprintsDetailVer!!.size==1)/* || (footprintsAfterVer!!.size==1 && footprintsDetailVer==null)*/ {
            holder.postStartIv.visibility = View.VISIBLE
            holder.postEndIv.visibility = View.VISIBLE
            holder.lineView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(lineMargin, lineMargin, lineMargin, lineMargin)
            }
        } else if (footprintsDetailVer==null && footprintsAfterVer!!.size==1) {
            holder.postStartIv.visibility = View.VISIBLE
            holder.postEndIv.visibility = View.VISIBLE
            holder.lineView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(lineMargin, lineMargin, lineMargin, lineMargin)
            }
        } else if (position == 0){
            holder.postStartIv.visibility = View.VISIBLE
            holder.postEndIv.visibility = View.INVISIBLE
            holder.lineView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(lineMargin, lineMargin, lineMargin, 0)
            }
        } else if (position == itemCount-1) {
            holder.postStartIv.visibility = View.INVISIBLE
            holder.postEndIv.visibility = View.VISIBLE
            holder.lineView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(lineMargin, 0, lineMargin, lineMargin)
            }
        } else {
            holder.postStartIv.visibility = View.INVISIBLE
            holder.postEndIv.visibility = View.INVISIBLE
            holder.lineView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(lineMargin, 0, lineMargin, 0)
            }
        }

        if (footprintsAfterVer==null)
            bindDetailVer(holder, position, footprintsDetailVer!![position])
        else
            bindAfterVer(holder, position, footprintsAfterVer!![position])
    }

    override fun getItemCount(): Int {
        return if (footprintsAfterVer==null)
            footprintsDetailVer!!.size
        else
            footprintsAfterVer!!.size
    }

    private fun bindAfterVer(holder: FootprintRVAdapter.PostViewHolder, position: Int, footprint: FootprintModel) {
        //기록 시간
        holder.postTimeTv.text = footprint.recordAt.split(" ")[1].substring(0, 5)

        //기록 편집 텍스트뷰 클릭 리스너
        holder.editTv.setOnClickListener {
            myItemClickListener.updateFootprintVerAfter(position, footprint)
        }

        //발자국 아이콘
        if (footprint.isMarked==1) {    //발자국 표시가 있는 발자국일 때
            holder.footPrintIv.visibility = View.VISIBLE
            holder.footPrintIv.setImageResource(footprintIcList[footprintIcIdx++])
        } else     //발자국 표시가 없는 발자국일 때(산책 종료 후 기록된 발자국)
            holder.footPrintIv.visibility = View.INVISIBLE

        //이미지가 있으면 뷰페이저 연결, 없으면 뷰페이저 연결 안함.
        if (footprint.photos.isEmpty()) {
            holder.photoVp.visibility = View.GONE
            holder.photoIndicator.visibility = View.GONE
        } else {
            val photoRVAdapter = PhotoRVAdapter(1)
            photoRVAdapter.addImgList(footprint.photos as ArrayList<String>)
            holder.photoVp.adapter = photoRVAdapter
            holder.photoVp.visibility = View.VISIBLE

            holder.photoIndicator.setViewPager(holder.photoVp)
            holder.photoIndicator.visibility = View.VISIBLE
        }

        //기록 내용
        val hashtagInContent = findHashTag(footprint.write)
        holder.contentTv.text = hashtagInContent

        //기록 내용 더보기, 간략히 보기
        holder.contentTv.post(Runnable {
            val lineCnt: Int = holder.contentTv.lineCount

            if (footprint.photos.isEmpty() && lineCnt > 3) {  //이미지가 없고, 기록 내용이 3줄보다 더 길 때
                holder.contentTv.maxLines = 3
                holder.viewMoreTv.visibility = View.VISIBLE
            } else if (footprint.photos.isNotEmpty() && lineCnt > 2) {    //이미지가 있고, 기록 내용이 2줄보다 더 길 때
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

                if (footprint.photos.isEmpty())
                    holder.contentTv.maxLines = 3
                else
                    holder.contentTv.maxLines = 2
            }
        }

        //기록 추가 UI VISIBLE
        holder.plusTv.visibility = View.VISIBLE
        holder.plusLine.visibility = View.VISIBLE

        //기록 추가 텍스트뷰 클릭 리스너
        holder.plusTv.setOnClickListener {
            myItemClickListener.addFootprint(position)
        }
    }

    private fun bindDetailVer(holder: FootprintRVAdapter.PostViewHolder, position: Int, footprint: Footprint) {
        holder.postTimeTv.text = footprint.recordAt //기록 시간

        //기록 편집 텍스트뷰 클릭 리스너
        holder.editTv.setOnClickListener {
            myItemClickListener.updateFootprintVerDetail(position, footprint)
        }

        //발자국 아이콘
        if (footprint.onWalk==1) {    //발자국 표시가 있는 발자국일 때
            holder.footPrintIv.visibility = View.VISIBLE
            holder.footPrintIv.setImageResource(footprintIcList[footprintIcIdx++])
        } else     //발자국 표시가 없는 발자국일 때(산책 종료 후 기록된 발자국)
            holder.footPrintIv.visibility = View.INVISIBLE

        //이미지가 있으면 뷰페이저 연결, 없으면 뷰페이저 연결 안함.
        if (footprint.photoList.isEmpty()) {
            holder.photoVp.visibility = View.GONE
            holder.photoIndicator.visibility = View.GONE
        } else {
            val photoRVAdapter = PhotoRVAdapter(1)
            photoRVAdapter.addImgList(footprint.photoList as ArrayList<String>)
            holder.photoVp.adapter = photoRVAdapter
            holder.photoVp.visibility = View.VISIBLE

            holder.photoIndicator.setViewPager(holder.photoVp)
            holder.photoIndicator.visibility = View.VISIBLE
        }

        //내용
        val hashtagInContent = findHashTag(footprint.write)
        holder.contentTv.text = hashtagInContent

        //기록 내용 더보기, 간략히 보기
        holder.contentTv.post(Runnable {
            val lineCnt: Int = holder.contentTv.lineCount

            if (footprint.photoList.isEmpty() && lineCnt > 3) {  //이미지가 없고, 기록 내용이 3줄보다 더 길 때
                holder.contentTv.maxLines = 3
                holder.viewMoreTv.visibility = View.VISIBLE
            } else if (footprint.photoList.isNotEmpty() && lineCnt > 2) {    //이미지가 있고, 기록 내용이 2줄보다 더 길 때
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

                if (footprint.photoList.isEmpty())
                    holder.contentTv.maxLines = 3
                else
                    holder.contentTv.maxLines = 2
            }
        }

        //기록 추가 UI INVISIBLE
        holder.plusTv.visibility = View.INVISIBLE
        holder.plusLine.visibility = View.INVISIBLE
    }

    private fun findHashTag(write: String): SpannableString {
        //발자국 내용 해시태그 폰트 색상 바꾸기
        var hashTagCnt = 0
        val hashtagInContent = SpannableString(write)
        val matcher: Matcher = Pattern.compile("#([A-Za-z0-9ㄱ-ㅎㅏ-ㅣ가-힣]+)").matcher(hashtagInContent)
        while (matcher.find() && hashTagCnt < 5) {
            hashTagCnt++    //5개까지만
            LogUtils.d("FootprintRVAdapter", "hashTagCnt: $hashTagCnt")

            hashtagInContent.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(binding.root.context, R.color.primary)),
                matcher.start(),
                matcher.end(),
                0
            )
        }

        return hashtagInContent
    }

    fun setDataAfterVer(footprints: ArrayList<FootprintModel>) {
        this.footprintsAfterVer = footprints

        footprintIcIdx = 0
        notifyDataSetChanged()
    }

    fun setDataDetailVer(footprints: ArrayList<Footprint>) {
        this.footprintsDetailVer = footprints

        footprintIcIdx = 0
        notifyDataSetChanged()
    }

    fun addData(footprint: FootprintModel, position: Int) {
        if (this.footprintsAfterVer!!.size == position)
            this.footprintsAfterVer!!.add(footprint)
        else
            this.footprintsAfterVer!!.add(position, footprint)

        footprintIcIdx = 0
        notifyDataSetChanged()
    }

    fun updateDataVerAfter(footprint: FootprintModel, position: Int) {
        footprintsAfterVer!![position] = footprint

        footprintIcIdx = 0
        notifyDataSetChanged()
    }

    fun setMyItemClickListener(myItemClickListener: MyItemClickListener) {
        this.myItemClickListener = myItemClickListener
    }

    inner class PostViewHolder(itemView: ItemFootprintBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val postStartIv: ImageView = itemView.itemFootprintPostStartIv
        val lineView: View = itemView.itemFootprintLineView
        val postTimeTv: TextView = itemView.itemFootprintPostTimeTv
        val photoVp: ViewPager2 = itemView.itemFootprintPhotoVp
        val photoIndicator: CircleIndicator3 = itemView.itemFootprintPhotoIndicator
        val contentTv: TextView = itemView.itemFootprintContentTv
        val viewMoreTv: TextView = itemView.itemFootprintViewMoreTv
        val footPrintIv: ImageView = itemView.itemFootprintFootprintIv
        val plusTv: TextView = itemView.itemFootprintPlusTv
        val plusLine: View = itemView.itemFootprintPlusLineView
        val editTv: TextView = itemView.itemFootprintEditTv
        val postEndIv: ImageView = itemView.itemFootprintPostEndIv
    }
}