package com.footprint.footprint.ui.main.home

import android.content.Intent
import android.util.Log
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.footprint.footprint.data.remote.weather.*
import com.footprint.footprint.data.remote.weather.ITEM
import com.footprint.footprint.data.remote.weather.WeatherResponse
import com.footprint.footprint.data.remote.weather.WeatherService
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class HomeFragment(): BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), WeatherView {

    //날씨 위한 변수
    var nx = "55"
    var ny = "127"
    lateinit var weatherService: WeatherService

    override fun initAfterBinding() {
        //WalkActivity로 가는 곳 
        binding.homeContentTv.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.startNextActivity(WalkActivity::class.java)
        }
        initTB()
        initDate()

        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
        var base_date = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(cal.time)
        var time = SimpleDateFormat("HH", Locale.KOREA).format(cal.time)
        val base_time = getTime(time)
        Log.d("info1", "날짜: ${base_date} 시간: ${base_time}")

        if(base_time >= "2000"){
            cal.add(Calendar.DATE, -1).toString()
            base_date = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(cal.time)
        }
        Log.d("info", "최종날짜: ${base_date} 최종시간: ${base_time}")

        weatherService = WeatherService(this)
        weatherService.getWeather(base_date, base_time, nx, ny)
    }


    private fun initDate() {
        //현재 날짜 받아오기
        val nowDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val dayOfWeek = when (nowDate.dayOfWeek.value) {
            1 -> '월'
            2 -> '화'
            3 -> '수'
            4 -> '목'
            5 -> '금'
            6 -> '토'
            7 -> '일'
            else -> ' '
        }
        binding.homeTopDateTv.text = String.format(
            "%d. %d. %d %c",
            nowDate.year,
            nowDate.month.value,
            nowDate.dayOfMonth,
            dayOfWeek
        )
    }

    private fun initTB() {
        val homeVPAdapter = HomeViewpagerAdapter(this)
        binding.homeDaymonthVp.adapter = homeVPAdapter
        
        val tbTitle = arrayListOf("일별", "월별")
        TabLayoutMediator(binding.homeDaymonthTb, binding.homeDaymonthVp) { tab, position ->
            tab.text = tbTitle[position]
        }.attach()
    }


    /*Function*/
    //*getTime()*
    //동네 예보 API는 3시간마다 현재 시각 + 4시간 뒤의 예보를 알려 줌
    //따라서, 현재 시간대의 날씨를 알기 위해 사용하는 함수
    private fun getTime(time: String): String{
        var result = when(time){
            in "00".."02" -> "2000" // 00~02
            in "03".."05" -> "2300" // 03~05
            in "06".."08" -> "0200" // 06~08
            in "09".."11" -> "0500" // 09~11
            in "12".."14" -> "0800" // 12~14
            in "16".."18" -> "1100" // 15~17
            in "18".."20" -> "1400" // 18~20
            else -> "1700"
        }

        return result
    }

    private fun getWeatherValue(rainType: String, sky: String): String{
        val result: String
        when(rainType){
            "0" -> {
                when(sky){
                    "1" -> result = "맑음"
                    "3" -> result = "구름 많음"
                    "4" -> result = "흐림"
                    else -> result = "null"
                }
            }
            "1" -> result = "비"
            "2" ->  result = "비/눈"
            "3" ->  result = "눈"
            "4" ->  result = "소나기"
            else ->  result = "null"
        }

        return result
    }

    override fun onWeatherLoading() {
        //로딩바띄우기
    }

    override fun onWeatherSuccess(items: List<ITEM>) {
        //온도 바꿔주기
        //이미지 바꾸기
        Log.d("response", items.toString())
        Toast.makeText(activity, "네", Toast.LENGTH_SHORT).show()

        val size = items.size
        var tmp: String = "0"
        var pty: String = "0"
        var sky: String = "0"
        for(i in 0 .. size-1){
            when(items[i].category){
                "TMP" -> tmp = items[i].fcstValue
                "PTY" -> pty = items[i].fcstValue
                "SKY" -> sky = items[i].fcstValue
            }
        }
        val weatherValue = getWeatherValue(pty, sky)

        //UI 변경
        binding.homeWeatherTempTv.text = tmp
        binding.homeWeatherConTv.text = weatherValue
    }

    override fun onWeatherFailure(code: Int, message: String) {
        //오류 메시지 띄우기 (토스트)
        Log.d("WEATHER/API2222", code.toString() + message)
    }


}