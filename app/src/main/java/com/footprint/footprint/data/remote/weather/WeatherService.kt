package com.footprint.footprint.data.remote.weather

import android.util.Log
import com.footprint.footprint.ui.main.home.HomeView
import com.footprint.footprint.ui.main.home.WeatherView
import com.footprint.footprint.utils.NetworkModule.Companion.getWeatherRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherService(private val view: WeatherView) {

    fun getWeather(base_date: String,base_time: String, nx: String, ny: String ) {
        val weatherService = getWeatherRetrofit()?.create(WeatherRetrofitInterface::class.java)

        val key =
            "V+1MlI+ZLP5u0ofnxvGVJTDzfOdPeQJLx1HCCC93OAP/McupiIw1U/+7E1OUAeVqcFEkqwgkdSRHiMReIf8zVA=="

        weatherService?.getWeather(key, 100, 1, "JSON", base_date, base_time, nx, ny)
            ?.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    val header = response.body()!!.response.header

                    Log.d("WEATHER/API-ASYNC", "Onresponse, resultCode는 ${header.resultCode} resultMsg는 ${header.resultMsg}")
                    when (header.resultCode) {
                        0 -> {
                            val it: List<ITEM> = response.body()!!.response.body.items.item
                            view.onWeatherSuccess(it)
                        }
                        else -> view.onWeatherFailure(header.resultCode, header.resultMsg)
                    }

                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    view.onWeatherFailure(213, t.message.toString())
                    Log.d("WEATHER/API-ERROR", t.message.toString())
                }

            })
    }

}