package com.footprint.footprint.ui.main.home

import android.content.Intent
import android.widget.Toast
import android.util.Log
import com.footprint.footprint.data.remote.weather.ApiObject
import com.footprint.footprint.data.remote.weather.ITEM
import com.footprint.footprint.data.remote.weather.WeatherResponse
import com.footprint.footprint.data.remote.weather.WeatherService
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class HomeFragment(): BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    //날씨 위한 변수
    var base_time = ""
    var base_date = ""
    var nx = ""
    var ny = ""

    override fun initAfterBinding() {
        binding.homeContentTv.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.startNextActivity(WalkActivity::class.java)
        }

        val tbTitle = arrayListOf("일별", "월별")
        val homeVPAdapter = HomeViewpagerAdapter(this)
        binding.homeDaymonthVp.adapter = homeVPAdapter

        TabLayoutMediator(binding.homeDaymonthTb, binding.homeDaymonthVp){ tab, position ->
            tab.text = tbTitle[position]
        }.attach()

        //현재 날짜 받아오기
        var nowDate = LocalDate.now(ZoneId.of("Asia/Seoul"))
        var dayOfWeek = when(nowDate.dayOfWeek.value){
            1 -> '월'
            2 -> '화'
            3 -> '수'
            4 -> '목'
            5 -> '금'
            6 -> '토'
            7 -> '일'
            else -> ' '
        }
        binding.homeTopDateTv.text = String.format("%d. %d. %d %c", nowDate.year, nowDate.month.value, nowDate.dayOfMonth, dayOfWeek)

        Toast.makeText(activity, "0", Toast.LENGTH_SHORT).show()

        val weatherService = WeatherService()
        weatherService.setWeatherView(this)

        /*준비*/
        //base_date(발표 날짜), base_time(발표 시각)
        //현재 날짜, 시간 정보 받아와서 설정
        val cal = Calendar.getInstance()
        base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time) //현재 날짜
        val time = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) //현재 시각
        base_time = getTime(time)

        Toast.makeText(activity, base_date, Toast.LENGTH_SHORT).show()

        //현재 시각이 00시가 넘었다면 어제 예보한 데이터를 가져와야 함 (현재 시간 + 4시간 뒤의 날씨 예보를 알려주기 때문)
        if(base_time >= "2000"){
            cal.add(Calendar.DATE, -1).toString() //hier
            base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        }

        /*날씨 불러오기*/
        //응답 형식: Json, 한 페이지 결과 수: 10, 페이지 번호: 1, 발표 날짜, 발표 시각, 예보지점 좌표
        val call = ApiObject.retrofitService.GetWeather( 10, 1, "JSON", base_date, base_time, nx, ny)

        Toast.makeText(activity, "2", Toast.LENGTH_SHORT).show()

        //비동기적으로 실행하기
        call.enqueue(object : Callback<WeatherResponse>{
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if(response.isSuccessful){
                    Toast.makeText(activity, "success", Toast.LENGTH_SHORT).show()
                    var it: List<ITEM> = response.body()!!.response.body.items.item

                    var sky = ""
                    var temp = ""

                    for(i in 0 .. 9){
                        when(it[i].category){
                            "SKY" -> sky = it[i].fcstValue
                            "TMP" -> temp = it[i].fcstValue
                            else -> continue
                        }
                    }


                    binding.homeWeatherTempTv.text = temp

                    var result_sky = ""
                    when(sky){
                        "1" -> result_sky = "맑음"
                        "3" -> result_sky = "구름"
                        "4" -> result_sky = "흐림"
                        else -> result_sky = "error"
                    }
                    Log.d("weather", "하늘 상태 ${sky}와 ${result_sky}와 기온 ${temp}")
                    Toast.makeText(activity, temp + sky + result_sky, Toast.LENGTH_LONG).show()
                }
            }

    /*private fun setMyClickListener() {
        binding.homeContentTv.setOnClickListener {
            startActivity(Intent(requireContext(), WalkAfterActivity::class.java))
        }
    }*/

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("weather-fail", t.message.toString())
                Toast.makeText(activity, "fail", Toast.LENGTH_SHORT).show()
                Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
            }

        })

        Toast.makeText(activity, "fin", Toast.LENGTH_SHORT).show()

        //weatherService.getWeather(base)

    }

    override fun onWeatherLoading() {

    }

    override fun onWeatherSuccess() {

    }

    override fun onWeatherFailure() {

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

}