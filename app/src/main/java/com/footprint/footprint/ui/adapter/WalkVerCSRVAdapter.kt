package com.footprint.footprint.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemCsTypeABinding
import com.footprint.footprint.databinding.ItemCsTypeBBinding
import com.footprint.footprint.ui.adapter.viewtype.WalkVerCSVT

class WalkVerCSRVAdapter(private val items: ArrayList<WalkVerCSVT>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface MyClickListener {
        fun click()
    }

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
            WalkVerCSVT.TYPE_A -> {
                // 이 위치에 view에 데이터를 적용시켜 주시면 됩니다.
            }
            WalkVerCSVT.TYPE_B -> {
                (holder as ViewHolderTypeB).nextIv.setOnClickListener {
                    myClickListener.click()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun getItemCount(): Int = items.size

    fun setMyClickListener(myClickListener: MyClickListener) {
        this.myClickListener = myClickListener
    }

    inner class ViewHolderTypeA(itemView: ItemCsTypeABinding): RecyclerView.ViewHolder(itemView.root) {
        init {

        }
    }

    inner class ViewHolderTypeB(itemView: ItemCsTypeBBinding): RecyclerView.ViewHolder(itemView.root) {
        val rv: RecyclerView = itemView.itemWalkCsRv
        val nextIv: ImageView = itemView.itemWalkCsNextIv

        init {
            rv.adapter = TagVerWalkCSRVAdapter()
        }
    }
}