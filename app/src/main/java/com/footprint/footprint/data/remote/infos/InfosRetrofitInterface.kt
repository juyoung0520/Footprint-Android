package com.footprint.footprint.data.remote.infos


import com.footprint.footprint.data.model.UserModel
import com.footprint.footprint.data.remote.user.InfoDetailResponse
import com.footprint.footprint.utils.GlobalApplication.Companion.X_ACCESS_TOKEN
import retrofit2.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InfosRetrofitInterface  {

    /*초기 정보 등록 API*/
    @POST("users/infos")
    fun registerInfos(@Body userModel: UserModel): Call<InfosResponse>

    /*마이페이지 정보 조회 API*/

    @GET("users/infos")
    fun getInfoDetail(): Call<InfoDetailResponse>
}