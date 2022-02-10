package com.footprint.footprint.ui.main.calendar

import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.databinding.FragmentSearchBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.TagRVAdapter
import com.footprint.footprint.ui.main.MainActivity

class SearchFragment(): BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private lateinit var adapter: TagRVAdapter
    private var isChanged = false //변경 사항 검사

    override fun initAfterBinding() {
        if(::adapter.isInitialized) {
            return
        }

        setBinding()
        initTagAdapter()
    }

    private fun setBinding() {
        binding.searchSearchBarEt.requestFocus()
        (activity as MainActivity).showKeyboardUp(binding.searchSearchBarEt)

        binding.searchBackIv.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchSearchBarEt.setOnEditorActionListener { textView, id, keyEvent ->
            if (id == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                searchTag()
            }
            true
        }

        // 검색 버튼 누르면 addTag
        binding.searchSearchIv.setOnClickListener {
            searchTag()
        }
    }

    private fun searchTag() {
        val result = replaceText(binding.searchSearchBarEt.text.toString())

        if (result != "") {
            adapter.addTag("#$result")

            if (!isChanged) {
                isChanged = true
            }

            binding.searchSearchBarEt.setText("")
            actionToSearchResultFragment("#$result")
        }

        (activity as MainActivity).hideKeyboard(binding.searchSearchBarEt)
    }

    private fun actionToSearchResultFragment(tag: String) {
        val action = SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(tag)
        findNavController().navigate(action)
    }

    private fun initTagAdapter() {
        adapter = TagRVAdapter(requireContext())
        binding.searchTagRv.adapter = adapter

        adapter.setOnItemClickListener(object : TagRVAdapter.OnItemClickListener {
            override fun onItemClick(tag: String) {
               actionToSearchResultFragment(tag)
            }
        })

        adapter.setOnItemRemoveClickListener(object : TagRVAdapter.OnItemRemoveClickListener{
            override fun onItemRemoveClick() {
                if (!isChanged) {
                    isChanged = true
                }
            }

        })
    }

    private fun replaceText(text: String): String {
        // 공백, #으로 시작 제거
        return text.replace(" ", "").replace(Regex("^#"),"")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (isChanged) {
            adapter.saveCurrentTags()
        }
    }
}