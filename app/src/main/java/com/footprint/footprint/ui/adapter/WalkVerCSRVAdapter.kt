package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footprint.footprint.databinding.ItemCsTypeABinding
import com.footprint.footprint.databinding.ItemCsTypeBBinding
import com.footprint.footprint.ui.adapter.viewtype.WalkVerCSVT

class WalkVerCSRVAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface MyClickListener {
        fun click(position: Int)
    }

    private var items: MutableList<WalkVerCSVT> = mutableListOf()

    private lateinit var bindingTypeA: ItemCsTypeABinding
    private lateinit var bindingTypeB: ItemCsTypeBBinding
    private lateinit var myClickListener: MyClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            WalkVerCSVT.TYPE_A -> {
                bindingTypeA = ItemCsTypeABinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolderTypeA(bindingTypeA)
            }
            else -> {
                bindingTypeB = ItemCsTypeBBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolderTypeB(bindingTypeB)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            WalkVerCSVT.TYPE_A -> {}
            WalkVerCSVT.TYPE_B -> {
                (holder as ViewHolderTypeB).run {
                    rv.adapter = TagVerWalkCSRVAdapter(items[position].data!!.hashtag)
                    Glide.with(pathIv.context).load(items[position].data!!.pathImageUrl).into(pathIv)
                    titleTv.text = "$position 번째 산책"
                    timeTv.text = items[position].data!!.walkTime

                    cardView.setOnClickListener {
                        myClickListener.click(position)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun getItemCount(): Int = items.size

    fun setData(items: MutableList<WalkVerCSVT>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun setMyClickListener(myClickListener: MyClickListener) {
        this.myClickListener = myClickListener
    }

    inner class ViewHolderTypeA(itemView: ItemCsTypeABinding): RecyclerView.ViewHolder(itemView.root) {
        init {

        }
    }

    inner class ViewHolderTypeB(itemView: ItemCsTypeBBinding): RecyclerView.ViewHolder(itemView.root) {
        val cardView: CardView = itemView.root
        val rv: RecyclerView = itemView.itemWalkCsRv
        val pathIv: ImageView = itemView.itemWalkCsThumbnailIv
        val titleTv: TextView = itemView.itemWalkCsTitleTv
        val timeTv: TextView = itemView.itemWalkCsWalkTimeTv
    }
}