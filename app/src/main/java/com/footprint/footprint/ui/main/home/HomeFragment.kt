package com.footprint.footprint.ui.main.home

import android.content.Intent
import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.footprint.footprint.R
import com.google.android.gms.location.*
import com.footprint.footprint.data.remote.weather.*
import com.footprint.footprint.data.remote.weather.ITEM
import com.footprint.footprint.data.remote.weather.WeatherService
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.onboarding.OnBoardingActivity
import com.footprint.footprint.ui.walk.WalkActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    WeatherView {

    lateinit var weatherService: WeatherService

    override fun initAfterBinding() {
        //산책 시작 버튼 => Walk Activity
        binding.homeStartBtn.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.startNextActivity(WalkActivity::class.java)
        }

        binding.homeSettingIv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }

        binding.homeDayGoalLayout.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.startNextActivity(OnBoardingActivity::class.java)
        }

        /*init: 1. TB&VP 2. 날짜*/
        initTB()
        initDate()

        /*init: 3. 날씨 */
        setPermission()   //위치 정보 사용 요청
        requestLocation() //날씨 API
    }

    /*Function*/
    //TabLayout과 Viewpager 연결
    private fun initTB() {
        val tbTitle = arrayListOf("일별", "월별")
        val homeVPAdapter = HomeViewpagerAdapter(this)
        binding.homeDaymonthVp.adapter = homeVPAdapter

        TabLayoutMediator(binding.homeDaymonthTb, binding.homeDaymonthVp) { tab, position ->
            tab.text = tbTitle[position]
        }.attach()

        val changeCB = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setGoal(position)
            }
        }
        binding.homeDaymonthVp.registerOnPageChangeCallback(changeCB)
    }

    //일별/월별 목표 보여주기
    private fun setGoal(position: Int) {
        if (position == 0) {
            binding.homeDayGoalLayout.visibility = View.VISIBLE
            binding.homeMonthGoalLayout.visibility = View.INVISIBLE
        } else if (position == 1) {
            binding.homeDayGoalLayout.visibility = View.INVISIBLE
            binding.homeMonthGoalLayout.visibility = View.VISIBLE
        }
    }

    //상단 날짜 받아오기
    private fun initDate() {
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

    /*상단 날씨 받아오기*/
    private fun setWeather(nx: Int, ny: Int) {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
        var base_date = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(cal.time) //date
        var time = SimpleDateFormat("HH", Locale.KOREA).format(cal.time) //hour
        val base_time = getTime(time)
        Log.d("WEATHER/DATE-BEFORE", "날짜: ${base_date} 시간: ${time}")

        if (base_time >= "2000") {
            cal.add(Calendar.DATE, -1).toString()
            base_date = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(cal.time)
        }
        Log.d("WEATHER/DATE-AFTER", "최종날짜: ${base_date} 최종시간: ${base_time}")


        weatherService = WeatherService(this)
        weatherService.getWeather(base_date, base_time, nx.toString(), ny.toString())
    }

    private fun getTime(time: String): String {
        return when (time) {
            in "00".."02" -> "2000" // 00~02
            in "03".."05" -> "2300" // 03~05
            in "06".."08" -> "0200" // 06~08
            in "09".."11" -> "0500" // 09~11
            in "12".."14" -> "0800" // 12~14
            in "16".."18" -> "1100" // 15~17
            in "18".."20" -> "1400" // 18~20
            else -> "1700"
        }
    }

    private fun setPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                //허용 시
                //Toast.makeText(activity, "권한 허용", Toast.LENGTH_SHORT).show()
                Log.d("WEATHER/PERMISSION-OK", "user GPS permission 허용")
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                //거절 시
                AlertDialog.Builder(activity).setMessage("권한 거절로 일부 기능이 제한됩니다.")
                    .setPositiveButton("권한 설정하러 가기") { dialog, which ->
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:com.footprint.footprint"))
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        }
                    }.show()
                //Toast.makeText(activity, "권한 거절", Toast.LENGTH_SHORT).show()
                Log.d("WEATHER/PERMISSION-NO", "user GPS permission 거절")
            }
        }

        //권한 설정
        TedPermission.create().setPermissionListener(permissionListener)
            .setRationaleMessage("정확한 날씨 정보를 위해 권한을 허용해 주세요")
            .setDeniedMessage("권한을 거부하셨습니다. [앱 설정] -> [권한]에서 허용해 주세요.")
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }
    private fun requestLocation() {
        val locationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        Log.d("WEATHER/LOCATION-REQUEST", "service 요청")
        try {
            val locationRequest = LocationRequest.create()
            locationRequest.run {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 60 * 60 * 1000 //요청 간격 1hour
                Log.d("WEATHER/LOCATION-REQUEST-OK", "위치 request")
            }
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    p0.let {
                        for (location in it.locations) {
                            val rs = TransLocalPoint().convertGRID_GPS(
                                0,
                                location.latitude,
                                location.longitude
                            )
                            Log.d("WEATHER/LOCATION-RESULT-LL", "위경도 "+location.toString())
                            Log.d("WEATHER/LOCATION-RESULT-XY", "변환된 좌표 rs.x: ${rs.x} rs.y: ${rs.y}")

                            setWeather(rs.x.toInt(), rs.y.toInt())
                        }
                    }

                }
            }

            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()!!
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun getWeatherValue(rainType: String, sky: String, wind: Int): String {
        val result: String
        if(wind > 13){
            result = "바람"
        }else{
            result = when (rainType) {
                "0" -> {
                    when (sky) {
                        "1" -> "맑음"
                        "3" -> "구름 많음"
                        "4" -> "흐림"
                        else -> "null"
                    }
                }
                "1" -> "비"
                "2" -> "비 또는 눈"
                "3" -> "눈"
                "4" -> "소나기"
                else -> "null"
            }
        }

        return result
    }


    /*WeatherView*/
    override fun onWeatherLoading() {
        //로딩바띄우기
    }

    override fun onWeatherSuccess(items: List<ITEM>) {
        Log.d("WEATHER/API-SUCCESS", items.toString())

        val size = items.size
        var tmp: String = "0"
        var pty: String = "0"
        var sky: String = "0"
        var wsd: Int = 0
        for (i in 0 until size) {
            when (items[i].category) {
                "TMP" -> tmp = items[i].fcstValue
                "PTY" -> pty = items[i].fcstValue
                "SKY" -> sky = items[i].fcstValue
                "WSD" -> sky = items[i].fcstValue
            }
        }
        val weatherValue = getWeatherValue(pty, sky, wsd)
        //UI 변경
        Log.d("WEATHERVALUE", "tmp: ${tmp} weatherValue: ${weatherValue}")
        if(binding.homeWeatherTempTv != null && binding.homeWeatherConTv != null){
            binding.homeWeatherTempTv.text = tmp
            binding.homeWeatherConTv.text = weatherValue
            val imgRes = when(weatherValue){
                "맑음" -> R.drawable.ic_weather_sunny
                "구름 많음" -> R.drawable.ic_weather_clounmany
                "흐림" -> R.drawable.ic_weather_cloud
                "소나기" -> R.drawable.ic_weather_shower
                "비" -> R.drawable.ic_weather_rain
                "눈" -> R.drawable.ic_weather_snowy
                "비 또는 눈" -> R.drawable.ic_weather_snoworrain
                "바람" -> R.drawable.ic_weather_windy
                else -> R.drawable.ic_weather_sunny
            }
            binding.homeTopWeatherIv.setImageResource(imgRes)
        }

    }

    override fun onWeatherFailure(code: Int, message: String) {
        //오류 메시지 띄우기
        Log.d("WEATHER/API-Failure", code.toString() + message)
    }

}