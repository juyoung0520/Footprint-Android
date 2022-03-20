package com.footprint.footprint.data.remote.walk

import com.footprint.footprint.data.model.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface WalkRetrofitInterface {
    /* calendarFragment */
    @GET("users/months/footprints")
    fun getMonthWalks(@Query("year")year: Int, @Query("month")month: Int): Call<MonthResponse>

    @GET("users/{date}")
    fun getDayWalks(@Path("date")date: String): Call<DayWalksResponse>

    @GET("users/tags")
    fun getTagWalkDates(@Query("tag")tag: String): Call<TagWalkDatesResponse>

    //산책 저장
    @Multipart
    @POST("walks")
    fun writeWalk(
        @Part("walk") walk: RequestBody,
        @Part("footprintList") footprintList: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): Call<WriteWalkResponse>

    //산책 정보 조회
    @GET("walks/{walkidx}")
    fun getWalk(@Path("walkidx") walkIdx: Int): Call<GetWalkResponse>

    //산책 정보 삭제
    @PATCH("walks/{walkIdx}/status")
    fun deleteWalk(@Path("walkIdx") walkIdx: Int): Call<BaseResponse>
}