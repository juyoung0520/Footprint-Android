package com.footprint.footprint.ui.main.calendar

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.UserDateWalkDTO
import com.footprint.footprint.data.remote.walk.*
import com.footprint.footprint.databinding.FragmentSearchResultBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.WalkDateRVAdapter
import com.footprint.footprint.ui.adapter.WalkRVAdapter
import com.footprint.footprint.utils.GlobalApplication.Companion.TAG
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.isNetworkAvailable
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchResultFragment() :
    BaseFragment<FragmentSearchResultBinding>(FragmentSearchResultBinding::inflate),
    SearchResultView {
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

        if (::currentTag.isInitialized) {
            adapter.setCurrentTag(currentTag)
        }

        adapter.setWalkClickListener(object : WalkRVAdapter.OnItemClickListener {
            override fun onItemClick(walk: UserDateWalkDTO) {
                val action =
                    SearchResultFragmentDirections.actionSearchResultFragmentToWalkDetailActivity(
                        walk.walkIdx
                    )
                findNavController().navigate(action)
            }
        })

        binding.searchResultWalkDatesRv.adapter = adapter
    }

    override fun onSearchResultLoading() {
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.searchResultLoadingPb.visibility = View.VISIBLE
                binding.searchResultHintTv.visibility = View.VISIBLE
                binding.searchResultWalkDatesRv.visibility = View.GONE
            })
        }
    }

    override fun onSearchResultFailure(code: Int, message: String) {
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                if (!isNetworkAvailable(requireContext())) {
                    showSnackBar(getString(R.string.error_network))
                } else {
                    showSnackBar(getString(R.string.error_api_fail))
                }
            })
        }
    }

    private fun showSnackBar(errorMessage: String) {
        Snackbar.make(
            requireView(),
            errorMessage,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(getString(R.string.action_retry)) {
            // Tag API
            WalkService.getTagWalkDates(this, currentTag.drop(1))
        }.show()
    }


    override fun onSearchResultSuccess(walkDates: List<WalkDateResult>) {
        LogUtils.d("$TAG/SEARCH-RESULT", "SEARCH-RESULT/WALK-DATES/success")

        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                binding.searchResultLoadingPb.visibility = View.GONE

                if (walkDates.isNotEmpty()) {
                    binding.searchResultHintTv.visibility = View.GONE
                    binding.searchResultWalkDatesRv.visibility = View.VISIBLE

                    initAdapter(walkDates)
                }
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