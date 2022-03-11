package com.footprint.footprint.data.remote.walk

import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.ui.main.calendar.CalendarView
import com.footprint.footprint.ui.main.calendar.SearchResultView
import com.footprint.footprint.ui.walk.WalkAfterView
import com.footprint.footprint.ui.walk.WalkDetailView
import com.footprint.footprint.utils.FormDataUtils
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.isNetworkAvailable
import gun0912.tedimagepicker.util.ToastUtil.context
import okhttp3.MultipartBody
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
        searchResultView.onSearchResultLoading()

        walkService.getTagWalkDates(tag).enqueue(object : Callback<TagWalkDatesResponse>{
            override fun onResponse(
                call: Call<TagWalkDatesResponse>,
                response: Response<TagWalkDatesResponse>
            ) {
                val resp = response.body()!!

                when (resp.code) {
                    1000 -> searchResultView.onSearchResultSuccess(resp.result!!)
                    else -> searchResultView.onSearchResultFailure(resp.code, resp.message)
                }
            }

            override fun onFailure(call: Call<TagWalkDatesResponse>, t: Throwable) {
                searchResultView.onSearchResultFailure(400, t.message!!)
            }

        })
    }

    //산책 정보 저장
    fun writeWalk(walkAfterView: WalkAfterView, walk: WalkModel) {
        walkAfterView.onWalkAfterLoading()

        val photosReq: ArrayList<MultipartBody.Part> = arrayListOf()    //서버에 전달할 이미지 리스트
        val saveWalkReq: SaveWalk = SaveWalk(startAt = walk.startAt, endAt = walk.endAt, distance = walk.distance, coordinates = walk.coordinate, calorie = walk.calorie)   //서버에 전달할 산책 정보 객체

        photosReq.add(FormDataUtils.prepareFilePart("photos", walk.pathImg)!!)    //산책 동선 사진을 이미지 리스트에 추가

        val footprintsReq: ArrayList<SaveFootprint> = arrayListOf() //서버에 전달할 발자국 데이터
        for (footprint in walk.footprints) {
            val data: SaveFootprint = SaveFootprint(
                footprint.coordinate,
                footprint.recordAt,
                footprint.write,
                footprint.hashtagList,
                footprint.isMarked
            )
            footprintsReq.add(data)

            //산책 정보의 photoMatchNumList 데이터 가공(각 발자국 별 저장된 이미지 갯수를 저장)
            if (footprint.photos.isEmpty()) {
                saveWalkReq.photoMatchNumList.add(0)
            } else {
                saveWalkReq.photoMatchNumList.add(footprint.photos.size)

                //이미지를 MultipartBody.Part 객체로 생성
                for (photo in footprint.photos)
                    photosReq.add(FormDataUtils.prepareFilePart("photos", photo)!!)
            }
        }

        LogUtils.d("WalkService", "writeWalk saveWalkReq: $saveWalkReq")
        LogUtils.d("WalkService", "writeWalk footprintsReq: $footprintsReq")
        LogUtils.d("WalkService", "writeWalk photosReq: $photosReq")

        val walkFormData = FormDataUtils.getJsonBody(saveWalkReq)!!   //산책 정보를 FormData 로 변환
        val footprintListFormData = FormDataUtils.getJsonBody(footprintsReq)!!    //발자국 정보를 FormData 로 변환

        //산책 저장 API 호출
        walkService.writeWalk(walkFormData, footprintListFormData, photosReq).enqueue(object : Callback<WriteWalkResponse> {
            override fun onResponse(
                call: Call<WriteWalkResponse>,
                response: Response<WriteWalkResponse>
            ) {
                val res = response.body()
                LogUtils.d("WalkService","\nwriteWalk-RES\ncode: ${res?.code}\nbody: ${res?.result}")

                when (val code = res?.code) {
                    1000 -> walkAfterView.onWriteWalkSuccess(res?.result!!)
                    else -> walkAfterView.onWalkAfterFail(code, walk)
                }
            }

            override fun onFailure(call: Call<WriteWalkResponse>, t: Throwable) {
                LogUtils.e("WalkService", "writeWalk-ERROR: ${t.message.toString()}")

                if (!isNetworkAvailable(context))
                    walkAfterView.onWalkAfterFail(6000, walk)
                else
                    walkAfterView.onWalkAfterFail(5000, walk)
            }
        })
    }

    //산책 정보 조회
    fun getWalk(walkDetailView: WalkDetailView, walkIdx: Int) {
        walkService.getWalk(walkIdx).enqueue(object : Callback<GetWalkResponse> {
            override fun onResponse(
                call: Call<GetWalkResponse>,
                response: Response<GetWalkResponse>
            ) {
                val res = response.body()
                LogUtils.d("WalkService","\ngetWalk-RES\ncode: ${res?.code}\nbody: ${res?.result}")

                if (res?.code==1000)
                    walkDetailView.onGetWalkSuccess(res?.result)
                else
                    walkDetailView.onWalkDetailGETFail(res?.code, walkIdx)
            }

            override fun onFailure(call: Call<GetWalkResponse>, t: Throwable) {
                LogUtils.e("WalkService", "getWalk-ERROR: ${t.message.toString()}")

                if (!isNetworkAvailable(context))
                    walkDetailView.onWalkDetailGETFail(6000, walkIdx)
                else
                    walkDetailView.onWalkDetailGETFail(5000, walkIdx)
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