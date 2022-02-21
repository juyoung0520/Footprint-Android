package com.footprint.footprint.data.remote.weather

import android.util.Log
import com.footprint.footprint.ui.main.home.HomeView
import com.footprint.footprint.utils.GlobalApplication.Companion.retrofit
import com.footprint.footprint.utils.LogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherService() {
    fun getWeather(homeView: HomeView, nx: String, ny:String){
        val weatherService = retrofit.create(WeatherRetrofitInterface::class.java)

        weatherService.getWeather(nx, ny).enqueue(object : Callback<WeatherResponse>{
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                val body = response.body()

                if(body != null) {
                    when (body.code) {
                        1000 -> homeView.onWeatherSuccess(body.result)
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