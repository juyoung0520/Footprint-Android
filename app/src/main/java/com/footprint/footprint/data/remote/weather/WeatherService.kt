package com.footprint.footprint.data.remote.weather

import android.util.Log
import com.footprint.footprint.ui.main.home.WeatherView
import com.footprint.footprint.utils.NetworkModule.Companion.getWeatherRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherService(view: WeatherView) {
    private val weatherView = view

    fun getWeather(base_date: String,base_time: String, nx: String, ny: String ) {
        val weatherService = getWeatherRetrofit()?.create(WeatherRetrofitInterface::class.java)

        weatherView.onWeatherLoading()
        val key =
            "V+1MlI+ZLP5u0ofnxvGVJTDzfOdPeQJLx1HCCC93OAP/McupiIw1U/+7E1OUAeVqcFEkqwgkdSRHiMReIf8zVA=="

        weatherService?.getWeather(key, 100, 1, "JSON", base_date, base_time, nx, ny)
            ?.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    val header = response.body()!!.response.header

                    Log.d("WEATHER/API", "hello")
                    when (header.resultCode) {
                        0 -> {
                            val it: List<ITEM> = response.body()!!.response.body.items.item
                            weatherView.onWeatherSuccess(it)
                        }
                        else -> weatherView.onWeatherFailure(header.resultCode, header.resultMsg)
                    }

                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    weatherView.onWeatherFailure(213, t.message.toString())
                    Log.d("WEATHER/API-ERROR", t.message.toString())
                }

            })
    }

}