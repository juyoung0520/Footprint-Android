package com.footprint.footprint.data.remote.walk

import com.footprint.footprint.ui.main.calendar.SearchResultView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.NetworkUtils
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object WalkService {
    private val walkService = retrofit.create(WalkRetrofitInterface::class.java)

    fun getTagWalkDates(searchResultView: SearchResultView, tag: String) {
        searchResultView.onSearchResultLoading()

        walkService.getTagWalkDates(tag).enqueue(object : Callback<TagWalkDatesResponse>{
            override fun onResponse(
                call: Call<TagWalkDatesResponse>,
                response: Response<TagWalkDatesResponse>
            ) {
                val resp = response.body()!!

                when (resp.code) {
                    1000 -> {
                        val listType = object : TypeToken<List<WalkDateResult>>() {}.type
                        searchResultView.onSearchResultSuccess(NetworkUtils.decrypt(resp.result, listType))
                    }
                    else -> searchResultView.onSearchResultFailure(resp.code, resp.message)
                }
            }

            override fun onFailure(call: Call<TagWalkDatesResponse>, t: Throwable) {
                searchResultView.onSearchResultFailure(400, t.message!!)
            }

        })
    }
}