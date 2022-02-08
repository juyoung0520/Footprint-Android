package com.footprint.footprint.ui.main.calendar

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.data.remote.walk.TagWalkDatesResponse
import com.footprint.footprint.data.remote.walk.UserDateWalk
import com.footprint.footprint.data.remote.walk.WalkDateResult
import com.footprint.footprint.data.remote.walk.WalkService
import com.footprint.footprint.databinding.FragmentSearchResultBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.WalkDateRVAdapter
import com.footprint.footprint.ui.adapter.WalkRVAdapter
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.utils.GlobalApplication.Companion.TAG
import com.google.gson.Gson

class SearchResultFragment() : BaseFragment<FragmentSearchResultBinding>(FragmentSearchResultBinding::inflate), SearchResultView{
    private lateinit var currentTag: String

    override fun initAfterBinding() {
        currentTag = navArgs<SearchResultFragmentArgs>().value.tag

        setBinding()

        // Tag API
        WalkService.getTagWalkDates(this, currentTag)
    }

    private fun setBinding() {
        binding.searchResultSearchBarTv.text = currentTag

        binding.searchResultSearchBarTv.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchResultBackIv.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchResultBackgroundV.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchResultSearchIv.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initAdapter(walkDates: List<WalkDateResult>) {
        val adapter = WalkDateRVAdapter(requireContext())

        adapter.setWalkDates(walkDates)
        adapter.setFragmentManager(requireActivity().supportFragmentManager)
        if (::currentTag.isInitialized) {
            adapter.setCurrentTag(currentTag)
        }

        adapter.setWalkDateRemoveListener(object : WalkDateRVAdapter.OnWalkDateRemoveListener {
            override fun onWalkDateRemove() {
                val itemCount = adapter.itemCount
                if (itemCount == 0) {
                    binding.searchResultHintTv.visibility = View.VISIBLE
                    binding.searchResultWalkDatesRv.visibility = View.GONE
                } else {
                    binding.searchResultHintTv.visibility = View.GONE
                    binding.searchResultWalkDatesRv.visibility = View.VISIBLE
                }
            }
        })

        binding.searchResultWalkDatesRv.adapter = adapter
    }

    override fun onSearchReaultLoading() {
        binding.searchResultLoadingPb.visibility = View.VISIBLE
        binding.searchResultHintTv.visibility = View.VISIBLE
        binding.searchResultWalkDatesRv.visibility = View.GONE
    }

    override fun onSearchReaultFailure(code: Int, message: String) {
        binding.searchResultLoadingPb.visibility = View.GONE

        when (code) {
            400 -> {
                Log.d("$TAG/SEARCH-RESULT/API", "SEARCH-RESULT/$message")
            }
            else -> {
                Log.d("$TAG/SEARCH-RESULT", "SEARCH-RESULT/$message")
            }
        }
    }

    override fun onSearchResultSuccess(walkDates: List<WalkDateResult>) {
        binding.searchResultLoadingPb.visibility = View.GONE
        binding.searchResultHintTv.visibility = View.GONE
        binding.searchResultWalkDatesRv.visibility = View.VISIBLE
        Log.d("$TAG/SEARCH-RESULT", "SEARCH-RESULT/success")
        initAdapter(walkDates)
    }
}