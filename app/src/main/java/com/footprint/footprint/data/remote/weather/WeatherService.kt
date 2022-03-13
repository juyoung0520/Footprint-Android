package com.footprint.footprint.data.remote.weather

import com.footprint.footprint.data.model.LocationModel
import com.footprint.footprint.ui.main.home.HomeView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherService() {
    fun getWeather(homeView: HomeView, nx: String, ny:String){
        val weatherService = retrofit.create(WeatherRetrofitInterface::class.java)

//        val location = LocationModel(nx, ny)
//        val encryptedData = NetworkUtils.encrypt(location)
//        val data = encryptedData.toRequestBody("application/json".toMediaType())
        val encryptedNx = NetworkUtils.encrypt(nx)
        val encryptedNy = NetworkUtils.encrypt(ny)

        weatherService.getWeather(encryptedNx, encryptedNy).enqueue(object : Callback<WeatherResponse>{
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                val body = response.body()

                if(body != null) {
                    when (body.code) {
                        1000 -> homeView.onWeatherSuccess(NetworkUtils.decrypt(body.result, Weather::class.java))
                        else -> homeView.onHomeFailure(body.code, body.message)
                    }
                }

            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                homeView.onHomeFailure(213, t.message.toString())
            }

        })
    }
}