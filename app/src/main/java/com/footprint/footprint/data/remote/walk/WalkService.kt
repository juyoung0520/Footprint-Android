package com.footprint.footprint.data.remote.walk

import com.footprint.footprint.data.dto.BaseResponse
import com.footprint.footprint.ui.main.calendar.CalendarView
import com.footprint.footprint.ui.main.calendar.SearchResultView
import com.footprint.footprint.ui.walk.WalkDetailView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.NetworkUtils
import com.footprint.footprint.utils.isNetworkAvailable
import com.google.gson.reflect.TypeToken
import gun0912.tedimagepicker.util.ToastUtil.context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object WalkService {
    private val walkService = retrofit.create(WalkRetrofitInterface::class.java)

    /* calendarFragment */
    fun getMonthWalks(calendarView: CalendarView, year: Int, month: Int) {
        calendarView.onMonthLoading()

        walkService.getMonthWalks(year, month).enqueue(object : Callback<MonthResponse> {
            override fun onResponse(call: Call<MonthResponse>, response: Response<MonthResponse>) {
                val resp= response.body()!!

                when (resp.code) {
                    1000 -> {
                        val listType = object : TypeToken<List<DayResult>>() {}.type
                        calendarView.onMonthSuccess(NetworkUtils.decrypt(resp.result, listType))
                    }
                    else -> calendarView.onCalendarFailure(resp.code, resp.message)
                }
            }

            override fun onFailure(call: Call<MonthResponse>, t: Throwable) {
                LogUtils.d("WALKSERVICE/MONTH", t.message.toString())
                calendarView.onCalendarFailure(400, t.message.toString())
            }

        })
    }

    fun getDayWalks(calendarView: CalendarView, day: String) {
        calendarView.onDayWalkLoading()

        walkService.getDayWalks(day).enqueue(object : Callback<DayWalksResponse> {
            override fun onResponse(
                call: Call<DayWalksResponse>,
                response: Response<DayWalksResponse>
            ) {
                val resp= response.body()!!
                //LogUtils.d("WALKSERVICE/DAY", resp.result)

                when (resp.code) {
                    1000 -> {
                        val listType = object : TypeToken<List<DayWalkResult>>() {}.type
                        calendarView.onDayWalksSuccess(NetworkUtils.decrypt(resp.result, listType))
                    }
                    else -> calendarView.onCalendarFailure(resp.code, resp.message)
                }
            }

            override fun onFailure(call: Call<DayWalksResponse>, t: Throwable) {
                LogUtils.d("WALKSERVICE/MONTH", t.message.toString())
                calendarView.onCalendarFailure(401, t.message.toString())
            }

        })
    }

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

    //산책 정보 삭제
    fun deleteWalk(walkDetailView: WalkDetailView, walkIdx: Int) {
        walkService.deleteWalk(walkIdx).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                updateFootprintResponse: Response<BaseResponse>
            ) {
                val res = updateFootprintResponse.body()
                LogUtils.d("WalkService","\ndeleteWalk-RES\ncode: ${res?.code}\nbody: ${res?.result}")

                if (res?.code==1000)
                    walkDetailView.onDeleteWalkSuccess()
                else
                    walkDetailView.onWalkDeleteFail(res?.code, walkIdx)
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                LogUtils.e("WalkService", "\ndeleteWalk-ERROR: ${t.message.toString()}")

                if (!isNetworkAvailable(context))
                    walkDetailView.onWalkDeleteFail(6000, walkIdx)
                else
                    walkDetailView.onWalkDeleteFail(5000, walkIdx)
            }

        })
    }

    // 캘린더 화면
    fun deleteWalk(calendarView: CalendarView, walkIdx: Int) {
        calendarView.onDayWalkLoading()

        walkService.deleteWalk(walkIdx).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                updateFootprintResponse: Response<BaseResponse>
            ) {
                val res = updateFootprintResponse.body()
                LogUtils.d("WalkService","\ndeleteWalk-RES\ncode: ${res?.code}\nbody: ${res?.result}")

                if (res?.code == 1000)
                    calendarView.onDeleteWalkSuccess()
                else
                    calendarView.onCalendarFailure(res?.code!!, res?.message!!)
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                calendarView.onCalendarFailure(5000, t.message.toString())
            }
        })
    }
}