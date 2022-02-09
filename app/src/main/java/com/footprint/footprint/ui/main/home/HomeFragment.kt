package com.footprint.footprint.ui.main.home

import android.content.Intent
import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.footprint.footprint.R
import com.footprint.footprint.data.remote.achieve.AchieveService
import com.footprint.footprint.data.remote.achieve.TMonth
import com.footprint.footprint.data.remote.achieve.Today
import com.footprint.footprint.data.remote.user.User
import com.footprint.footprint.data.remote.user.UserService
import com.google.android.gms.location.*
import com.footprint.footprint.data.remote.weather.*
import com.footprint.footprint.data.remote.weather.ITEM
import com.footprint.footprint.data.remote.weather.WeatherService
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.HomeViewpagerAdapter
import com.footprint.footprint.ui.agree.AgreeActivity
import com.footprint.footprint.ui.main.MainActivity
import com.footprint.footprint.ui.register.RegisterActivity
import com.footprint.footprint.ui.walk.WalkActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    WeatherView, HomeView, HomeDayView, HomeMonthView {

    private lateinit var homeVPAdapter: HomeViewpagerAdapter
    private val fragmentList = arrayListOf<Fragment>(HomeDayFragment(), HomeMonthFragment())

    private var walkGoalTime = 0

    lateinit var weatherService: WeatherService
    private var jobs: ArrayList<Job> = arrayListOf()

    override fun initAfterBinding() {
        if (!::homeVPAdapter.isInitialized)
            initTB()
        initDate()

        setClickListener()

        setPermission()   //위치 정보 사용 요청
        requestLocation() //날씨 API
    }

    private fun setClickListener() {
        //산책 시작 버튼 => Walk Activity
        binding.homeStartBtn.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.startNextActivity(WalkActivity::class.java)
        }

        //설정 버튼 -> 설정 프래그먼트
        binding.homeSettingIv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AchieveService.setHomeView(this)

        //유저 닉네임 -> 한번만 호출
        UserService.getUser(this)
    }


    override fun onStart() {
        super.onStart()
        //일별, 월별 -> 홈프래그먼트 돌아올 때마다 호출
        AchieveService.getToday(this)
        AchieveService.getTMonth(this)
    }

    /*Function*/
    //TabLayout과 Viewpager 연결
    private fun initTB() {
        val tbTitle = arrayListOf("일별", "월별")
        homeVPAdapter = HomeViewpagerAdapter(this, fragmentList)
        binding.homeDaymonthVp.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
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

    //위치 정보 권한 허용 함수
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

    /*상단 날씨 받아오기: nx, ny, time*/
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

        weatherService = WeatherService(this@HomeFragment)
        weatherService.getWeather(base_date, base_time, nx.toString(), ny.toString())

    }

    //시간 결정 함수(time)
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

    //GPS로 위치 받아오는 함수(nx, ny)
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
                            Log.d("WEATHER/LOCATION-RESULT-LL", "위경도 " + location.toString())
                            Log.d(
                                "WEATHER/LOCATION-RESULT-XY",
                                "변환된 좌표 rs.x: ${rs.x} rs.y: ${rs.y}"
                            )

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

    //하늘 상태 결정해 주는 함수
    private fun getWeatherValue(rainType: String, sky: String, wind: Int): String {
        val result: String
        if (wind > 13) {
            result = "바람"
        } else {
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


    /*날씨 API*/
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
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {//visibility 조절
                binding.homeTopLineIv.visibility = View.VISIBLE
                binding.homeTopWeatherIv.visibility = View.VISIBLE
                binding.homeWeatherTempTv.visibility = View.VISIBLE
                binding.homeWeatherUnitTv.visibility = View.VISIBLE
                binding.homeWeatherConTv.visibility = View.VISIBLE

                binding.homeWeatherTempTv.text = tmp
                binding.homeWeatherConTv.text = weatherValue
                val imgRes = when (weatherValue) {
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
            })
        }

    }

    override fun onWeatherFailure(code: Int, message: String) {
        Log.d("WEATHER/API-FAILURE", "code: $code message: $message")
    }

    /*유저 정보 조회 API*/
    override fun onUserSuccess(user: User) {
        Log.d("HOME(USER)/API-SUCCESS", user.toString())

        //닉네임 바꿔주기
        binding.homeTopUsernameTv.text = user.nickname
    }
    override fun onUserFailure(code: Int, message: String) {
        Log.d("HOME(USER)/API-FAILURE", "code: $code message: $message")
    }


    /*일별 정보 조회 API*/
    override fun onTodaySuccess(today: Today) {
        Log.d("HOME(TODAY)/API-SUCCESS", today.toString())
        walkGoalTime = today.walkGoalTime
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                //목표 바꿔주기
                val color =
                    if (today.walkTime >= today.walkGoalTime) R.color.secondary else R.color.black
                binding.homeMonthGoalWalkTv.setTextColor(resources.getColor(color))
                binding.homeDayGoalWalkTv.text = today.walkTime.toString()
                binding.homeDayGoalWalkTv.isSelected = true
                binding.homeDayGoalDistTv.text = today.distance.toString()
                binding.homeDayGoalDistTv.isSelected = true
                binding.homeDayGoalKcalTv.text = today.calorie.toString()
                binding.homeDayGoalKcalTv.isSelected = true
            })
        }

        // -> HomeDayFragment
        (fragmentList[0] as HomeDayFragment).onTodaySuccess(today)
    }
    override fun onTodayFailure(code: Int, message: String) {
        Log.d("HOME(TODAY)/API-FAILURE", code.toString() + message)
    }

    /*월별 정보 조회 API*/
    override fun onTMonthSuccess(tMonth: TMonth) {
        Log.d("HOME(TMONTH)/API-SUCCESS", tMonth.toString())

        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                //누적 산책시간
                val monthTotalMin = tMonth.getMonthTotal.monthTotalMin
                val color = if(monthTotalMin > walkGoalTime) "#FFC01D" else "#241F20"
                binding.homeMonthGoalWalkTv.setTextColor(Color.parseColor(color))
                binding.homeMonthGoalWalkTv.text = monthTotalMin.toString()
                binding.homeMonthGoalWalkTv.isSelected = true
                //누적 거리
                binding.homeMonthGoalDistTv.text = tMonth.getMonthTotal.monthTotalDistance.toString()
                binding.homeMonthGoalDistTv.isSelected = true
                //누적 칼로리
                binding.homeMonthGoalKcalTv.text = tMonth.getMonthTotal.monthPerCal.toString()
                binding.homeMonthGoalKcalTv.isSelected = true
            })
        }

        // -> HomeMonthFragment
        (fragmentList[1] as HomeMonthFragment).onTMonthSuccess(tMonth)
    }
    override fun onTMonthFailure(code: Int, message: String) {
        Log.d("HOME(TMONTH)/API-FAILURE", code.toString() + message)
    }

    override fun onDestroyView() {
        //등록된 jobs cancel -> binding error 막기 위해
        for (job in jobs) {
            job.cancel()
        }
        super.onDestroyView()
    }
}