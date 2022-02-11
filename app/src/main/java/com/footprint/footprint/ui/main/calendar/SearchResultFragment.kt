package com.footprint.footprint.ui.main.calendar

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchResultFragment() : BaseFragment<FragmentSearchResultBinding>(FragmentSearchResultBinding::inflate), SearchResultView{
    private var isInitialized = false
    private lateinit var currentTag: String
    private val jobs = arrayListOf<Job>()

    override fun initAfterBinding() {
        if (!isInitialized) {
            currentTag = navArgs<SearchResultFragmentArgs>().value.tag
            setBinding()

            isInitialized = true
        }

        // Tag API
        WalkService.getTagWalkDates(this, currentTag.drop(1))
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

        adapter.setWalkClickListener(object : WalkRVAdapter.OnItemClickListener {
            override fun onItemClick(walk: UserDateWalk) {
                val action = SearchResultFragmentDirections.actionSearchResultFragmentToWalkDetailActivity(walk.walkIdx)
                findNavController().navigate(action)
            }
        })

        binding.searchResultWalkDatesRv.adapter = adapter
    }

    override fun onSearchReaultLoading() {
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.searchResultLoadingPb.visibility = View.VISIBLE
                binding.searchResultHintTv.visibility = View.VISIBLE
                binding.searchResultWalkDatesRv.visibility = View.GONE
            })
        }
    }

    override fun onSearchReaultFailure(code: Int, message: String) {
        when (code) {
            400 -> {
                Log.d("$TAG/SEARCH-RESULT", "SEARCH-RESULT/fail/$message")
            }
            else -> {
                Log.d("$TAG/SEARCH-RESULT", "SEARCH-RESULT/fail/$message")
            }
        }

        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.searchResultLoadingPb.visibility = View.GONE
            })
        }
    }

    override fun onSearchResultSuccess(walkDates: List<WalkDateResult>) {
        Log.d("$TAG/SEARCH-RESULT", "SEARCH-RESULT/WALK-DATES/success")

        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.searchResultLoadingPb.visibility = View.GONE
                binding.searchResultHintTv.visibility = View.GONE
                binding.searchResultWalkDatesRv.visibility = View.VISIBLE

                initAdapter(walkDates)
            })
        }
    }

    override fun onDeleteWalkSuccess() {
        Log.d("$TAG/SEARCH-RESULT", "SEARCH-RESULT/DELETE-WALK/success")

        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                WalkService.getTagWalkDates(this@SearchResultFragment, currentTag.drop(1))
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        jobs.map {
            it.cancel()
        }
    }
}