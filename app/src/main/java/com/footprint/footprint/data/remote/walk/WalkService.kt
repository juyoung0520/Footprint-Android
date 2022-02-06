package com.footprint.footprint.data.remote.walk

import android.util.Log
import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.ui.main.calendar.CalendarView
import com.footprint.footprint.ui.main.calendar.SearchResultView
import com.footprint.footprint.utils.FormDataUtils
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import okhttp3.MultipartBody
import retrofit2.*

object WalkService {
    /* calendarFragment */
    fun getMonthWalks(calendarView: CalendarView, year: Int, month: Int) {
        val walkService = retrofit.create(WalkRetrofitInterface::class.java)

        calendarView.onMonthLoading()

        walkService.getMonthWalks(year, month).enqueue(object : Callback<MonthResponse> {
            override fun onResponse(call: Call<MonthResponse>, response: Response<MonthResponse>) {
                val resp= response.body()!!

                when (resp.code) {
                    1000 -> calendarView.onMonthSuccess(resp.result)
                    else -> calendarView.onCalendarFailure(resp.code, resp.message)
                }
            }

            override fun onFailure(call: Call<MonthResponse>, t: Throwable) {
                calendarView.onCalendarFailure(400, t.message.toString())
            }

        })
    }

    fun getDayWalks(calendarView: CalendarView, day: String) {
        val walkService = retrofit.create(WalkRetrofitInterface::class.java)

        calendarView.onDayWalkLoading()

        walkService.getDayWalks(day).enqueue(object : Callback<DayWalksResponse> {
            override fun onResponse(
                call: Call<DayWalksResponse>,
                response: Response<DayWalksResponse>
            ) {
                val resp= response.body()!!

                when (resp.code) {
                    1000 -> calendarView.onDayWalksSuccess(resp.result)
                    else -> calendarView.onCalendarFailure(resp.code, resp.message)
                }
            }

            override fun onFailure(call: Call<DayWalksResponse>, t: Throwable) {
                calendarView.onCalendarFailure(401, t.message.toString())
            }

        })
    }

    fun getTagWalkDates(searchResultView: SearchResultView, tag: String) {
        val walkService = retrofit.create(WalkRetrofitInterface::class.java)

        searchResultView.onSearchReaultLoading()

        walkService.getTagWalkDates(tag).enqueue(object : Callback<TagWalkDatesResponse>{
            override fun onResponse(
                call: Call<TagWalkDatesResponse>,
                response: Response<TagWalkDatesResponse>
            ) {
                val resp = response.body()!!

                when (resp.code) {
                    1000 -> searchResultView.onSearchResultSuccess(resp.result!!)
                    else -> searchResultView.onSearchReaultFailure(resp.code, resp.message)
                }
            }

            override fun onFailure(call: Call<TagWalkDatesResponse>, t: Throwable) {
                searchResultView.onSearchReaultFailure(400, t.message!!)
            }

        })
    }

    fun writeWalk(walk: WalkModel) {
        val photosReq: ArrayList<MultipartBody.Part> = arrayListOf()
        val saveWalkReq: SaveWalk =
            SaveWalk(startAt = walk.startAt, endAt = walk.endAt, distance = walk.distance, coordinates = walk.coordinate, calorie = walk.calorie, userIdx = 1)

        photosReq.add(FormDataUtils.prepareFilePart("photos", walk.pathImg))

        val footprintsReq: ArrayList<SaveFootprint> = arrayListOf()
        for (footprint in walk.footprints) {
            val data: SaveFootprint = SaveFootprint(
                footprint.coordinate,
                footprint.recordAt,
                footprint.write,
                footprint.hashtagList
            )
            footprintsReq.add(data)

            if (footprint.photos.isEmpty())
                saveWalkReq.photoMatchNumList.add(0)
            else {
                saveWalkReq.photoMatchNumList.add(footprint.photos.size)

                for (photo in footprint.photos)
                    photosReq.add(FormDataUtils.prepareFilePart("photos", photo))
            }
        }

        Log.d("WalkService", "\nwriteWalk\nsaveWalkReq: $saveWalkReq\nfootprintsReq: $footprintsReq")

        val walkFormData = FormDataUtils.getJsonBody(saveWalkReq)
        val footprintListFormData = FormDataUtils.getJsonBody(footprintsReq)

        walkService.writeWalk(walkFormData, footprintListFormData, photosReq).enqueue(object : Callback<WriteWalkResponse> {
            override fun onResponse(
                call: Call<WriteWalkResponse>,
                response: Response<WriteWalkResponse>
            ) {
                val res = response.body()
                Log.d("WalkService","writeWalk-RES: ${res?.code}")
            }

            override fun onFailure(call: Call<WriteWalkResponse>, t: Throwable) {
                Log.e("WalkService", "writeWalk-ERROR: ${t.message.toString()}")
            }
        })
    }

}