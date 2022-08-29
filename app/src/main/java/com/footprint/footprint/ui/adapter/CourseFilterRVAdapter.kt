package com.footprint.footprint.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemFilterBinding
import com.footprint.footprint.databinding.ItemOrderbyBinding
import com.footprint.footprint.domain.model.FilteringMode
import com.footprint.footprint.domain.model.FilteringModel
import com.footprint.footprint.ui.dialog.FilterBottomDialog
import com.footprint.footprint.ui.dialog.OrderByBottomDialog
import com.footprint.footprint.utils.SEARCH_IN
import com.footprint.footprint.utils.isDefault
import com.google.gson.Gson

// 필터 state 관리
// Update 시, MyListener 통해 CourseFrg -> CourseVm 으로 전달 (업데이트)
class CourseFilterRVAdapter(private val fragmentManager: FragmentManager, private val filtering: ArrayList<FilteringModel>, private val filterState: HashMap<String, Int?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class OrderByViewHolder(val binding: ItemOrderbyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(filter: FilteringModel, position: Int) {

            // 타이틀 및 색상 설정
            binding.itemOrderbyTv.text = filter.contents[filterState[filter.title]!!]

            if(isDefault(filter.type, filterState[filter.title])){
                binding.root.isSelected = false
                binding.itemOrderbyTv.isSelected = false
                binding.itemOrderbyIv.isSelected = false
            }else{
                binding.root.isSelected = true
                binding.itemOrderbyTv.isSelected = true
                binding.itemOrderbyIv.isSelected = true
            }

            // 클릭 리스너
            binding.root.setOnClickListener {

                val bottomDialog = OrderByBottomDialog()
                val bundle = Bundle().apply {
                    putString("orderBy", Gson().toJson(filter))
                    putInt("selected", filterState[filter.title]!!)
                }
                bottomDialog.arguments = bundle
                bottomDialog.show(fragmentManager, bottomDialog.tag)

                bottomDialog.setMyItemClickListener(object : OrderByBottomDialog.OnDismissListener {
                    override fun onDismiss(selected: Int) {
                        filterState[filter.title] = selected
                        binding.itemOrderbyTv.text = filter.contents[selected]

                        // 바뀐 기준 알려주기
                        myListener.onChange()
                        if(filter.title == SEARCH_IN) myListener.onModeChange(filter.contents[selected])
                        notifyItemChanged(position)
                    }
                })
            }
        }
    }

    inner class FilterViewHolder(val binding: ItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(filter: FilteringModel, position: Int) {

            // 타이틀 설정정
            val title = if(filterState[filter.title] == null) filter.title else filter.contents[filterState[filter.title]!!]
            binding.itemFilterTv.text = title

            // 색상 설정
            if(isDefault(filter.type, filterState[filter.title])){
                binding.root.isSelected = false
                binding.itemFilterTv.isSelected = false
                binding.itemFilterIv.isSelected = false
            }else{
                binding.root.isSelected = true
                binding.itemFilterTv.isSelected = true
                binding.itemFilterIv.isSelected = true
            }

            // 클릭 리스너
            binding.root.setOnClickListener {

                // Bottom Dialog 띄워줌
                val bottomDialog = FilterBottomDialog()
                val bundle = Bundle().apply {
                    putString("filter", Gson().toJson(filter))
                    putInt("selected", filterState[filter.title] ?: 0)
                }
                bottomDialog.arguments = bundle
                bottomDialog.show(fragmentManager, bottomDialog.tag)

                bottomDialog.setMyItemClickListener(object : FilterBottomDialog.OnDismissListener{
                    override fun onSelected(selected: Int) {
                        filterState[filter.title] = selected
                        binding.itemFilterTv.text = filter.contents[selected]

                        myListener.onChange()
                        notifyItemChanged(position)
                    }

                    override fun onReset() {
                        filterState[filter.title] = null

                        myListener.onChange()
                        notifyItemChanged(position)
                    }
                })
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return filtering[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FilteringMode.FILTER ->
                FilterViewHolder(
                    ItemFilterBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            else ->
                OrderByViewHolder(
                    ItemOrderbyBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (filtering[position].type) {
            FilteringMode.FILTER ->
                (holder as FilterViewHolder).bind(filtering[position], position)
            else ->
                (holder as OrderByViewHolder).bind(filtering[position], position)
        }
    }

    override fun getItemCount(): Int = filtering.size

    /* 클릭 이벤트 관리 */
    interface MyListener {
        fun onChange()
        fun onModeChange(mode: String)
    }

    private lateinit var myListener: MyListener

    fun setMyListener(myListener: MyListener) {
        this.myListener = myListener
    }

    /* 상태 관리 */
    fun reset(initialState: HashMap<String, Int?> ){
        for(key in filterState.keys){
            filterState[key] = initialState[key]
        }

        notifyDataSetChanged()
    }
}