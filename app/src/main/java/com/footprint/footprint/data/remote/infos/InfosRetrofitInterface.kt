package com.footprint.footprint.data.remote.infos


import com.footprint.footprint.data.model.UserModel
import retrofit2.*
import retrofit2.http.Body
import retrofit2.http.POST

interface InfosRetrofitInterface  {

    /*초기 정보 등록 API*/
    @POST("users/infos")
    fun registerInfos(@Body userModel: UserModel): Call<InfosResponse>

    /*마이페이지 정보 조회 API*/

}