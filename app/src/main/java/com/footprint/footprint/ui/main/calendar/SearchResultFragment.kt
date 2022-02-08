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
    private lateinit var currentTag: String
    private val jobs = arrayListOf<Job>()

    override fun initAfterBinding() {
        currentTag = navArgs<SearchResultFragmentArgs>().value.tag

        setBinding()

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
                Log.d("$TAG/SEARCH-RESULT/API", "SEARCH-RESULT/$message")
            }
            else -> {
                Log.d("$TAG/SEARCH-RESULT", "SEARCH-RESULT/$message")
            }
        }

        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.searchResultLoadingPb.visibility = View.GONE
            })
        }
    }

    override fun onSearchResultSuccess(walkDates: List<WalkDateResult>) {
        Log.d("$TAG/SEARCH-RESULT", "SEARCH-RESULT/success")

        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.searchResultLoadingPb.visibility = View.GONE
                binding.searchResultHintTv.visibility = View.GONE
                binding.searchResultWalkDatesRv.visibility = View.VISIBLE

                initAdapter(walkDates)
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