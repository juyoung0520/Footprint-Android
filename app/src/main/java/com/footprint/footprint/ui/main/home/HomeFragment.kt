package com.footprint.footprint.ui.main.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.footprint.footprint.R
import com.footprint.footprint.data.model.UserInfoModel
import com.footprint.footprint.data.model.UserModel
import com.footprint.footprint.data.remote.achieve.AchieveService
import com.footprint.footprint.data.remote.achieve.TMonth
import com.footprint.footprint.data.remote.achieve.Today
import com.footprint.footprint.data.remote.user.User
import com.footprint.footprint.data.remote.user.UserService
import com.footprint.footprint.data.remote.weather.*
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.HomeViewpagerAdapter
import com.footprint.footprint.ui.walk.WalkActivity
import com.footprint.footprint.utils.*
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    HomeView, HomeDayView, HomeMonthView {

    //뷰페이저, 프래그먼트
    private lateinit var homeVPAdapter: HomeViewpagerAdapter
    private lateinit var gpsDescDialog: AlertDialog
    private var backgroundGPSDialog: AlertDialog? = null
    private val fragmentList = arrayListOf<Fragment>(HomeDayFragment(), HomeMonthFragment())
    private val gpsBackgroundPermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            //허용 시
            LogUtils.d("WEATHER/PERMISSION-OK", "user GPS Background permission 허용")
            backgroundGPSDialog?.dismiss()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            LogUtils.d("WEATHER/PERMISSION-NO", "user GPS Background permission 거절")
            backgroundGPSDialog?.dismiss()
        }
    }

    //Walk 액티비티로 전달할 유저 정보
    private var userInfo = UserInfoModel()

    //lifecycleScope 저장해두는 jobs
    private var jobs: ArrayList<Job> = arrayListOf()

    override fun initAfterBinding() {
        val gpsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getString(R.string.msg_foreground_gps)
        } else {
            getString(R.string.msg_gps)
        }

        gpsDescDialog = this?.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.apply {
                setTitle(getString(R.string.title_notification))
                setMessage(gpsMessage)
                setPositiveButton(R.string.action_agree) { dialog, id ->
                    setPermission()    //위치 정보 사용 요청(포그라운드)
                }
                setNegativeButton(R.string.action_not_agree) { dialog, id ->
                    showGPSDeniedDialog(getString(R.string.msg_denied_foreground_gps))  //거부 관련 다이얼로그 띄우기
                }
            }
            builder.create()
        }

        //백그라운드 위치 권한 관련 다이얼로그
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundGPSDialog = this.let {
                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.apply {
                    setMessage(getString(R.string.msg_background_gps))
                    setPositiveButton(R.string.action_change_gps_access) { dialog, id ->
                        TedPermission.create()
                            .setPermissionListener(gpsBackgroundPermissionListener)
                            .setPermissions(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            .check()
                    }
                    setNegativeButton(R.string.action_maintain_gps_access, null)
                }
                builder.create()
            }
        }

        if (!getBackgroundGPS()) {  //첫 사용자에게 위치 서비스 사용에 대한 안내 다이얼로그 띄우기
            saveBackgroundGPS(true)
            gpsDescDialog.show()
        } else
            setPermission()    //위치 정보 사용 요청

        setClickListener() //클릭 이벤트 설정

        //Initialize
        initTB()
        initDate()
    }

    override fun onStart() {
        super.onStart()
        //날씨 API
        callWeatherAPI()

        //유저 조회 API
        UserService.getUser(this)

        //일별
        AchieveService.setHomeView(this)
        AchieveService.getToday(this)
    }

    private fun setClickListener() {
        //산책 시작 버튼 => Walk Activity
        binding.homeStartBtn.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showGPSDeniedDialog(getString(R.string.msg_denied_walk_for_foreground_gps))

                return@setOnClickListener
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showGPSDeniedDialog(getString(R.string.msg_denied_walk_for_background_gps))

                return@setOnClickListener
            }

            //유저 정보가 다 채워져야 산책 시작 가능
            if (userInfo.height != null && userInfo.weight != null && userInfo.walkNumber != null) {
                val intent = Intent(activity, WalkActivity::class.java)

                val userInfoJson = Gson().toJson(userInfo)
                intent.putExtra("userInfo", userInfoJson)

                LogUtils.d(
                    "userInfo",
                    "목표 시간: ${userInfo.goalWalkTime} 키: ${userInfo.height} 몸무게: ${userInfo.weight} 산책횟수:  ${userInfo.walkNumber}"
                )
                startActivity(intent)
            } else { //정보 없음
                Toast.makeText(activity, "다시 시도해 주세요", Toast.LENGTH_SHORT).show()
            }
        }

        //설정 버튼 -> 설정 프래그먼트
        binding.homeSettingIv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }
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

    //위치 정보 권한 허용 함수(포그라운드)
    private fun setPermission() {
        val gpsForegroundPermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                //허용 시
                LogUtils.d("WEATHER/PERMISSION-OK", "user GPS permission 허용")

                if (!getFirstBackgroundGPSCheck()) {    //처음 들어온 사용자 -> 백그라운드 위치 권한 다이얼로그 띄우기
                    saveFirstBackgroundGPSCheck(true)

                    backgroundGPSDialog?.show()
                }
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                LogUtils.d("WEATHER/PERMISSION-NO", "user GPS permission 거절")

                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                )
                    showGPSDeniedDialog(getString(R.string.msg_denied_foreground_gps))
            }
        }

        //권한 설정
        TedPermission.create().setPermissionListener(gpsForegroundPermissionListener)
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }

    //날씨 API 호출
    private fun callWeatherAPI() {

        //현재 위치 불러오기
        val locationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        try {
            val locationRequest = LocationRequest.create()
            locationRequest.run {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 60 * 60 * 60 //1시간마다 위치 불러옴
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

                            LogUtils.d("WEATHER/API-READY", "rs: ${rs.x.toInt()}, ${rs.y.toInt()}")
                            //날씨 api 호출
                            WeatherService().getWeather(
                                this@HomeFragment,
                                rs.x.toInt().toString(),
                                rs.y.toInt().toString()
                            )
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

    private fun showGPSDeniedDialog(msg: String) {
        val gpsDeniedDialog: AlertDialog = this?.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.apply {
                setTitle(getString(R.string.title_notification))
                setMessage(msg)
                setPositiveButton(getString(R.string.action_confirm), null)
            }
            builder.create()
            builder.show()
        }
    }

    /*유저 정보 조회 API*/
    override fun onUserSuccess(user: User) {
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                //닉네임 바꿔주기
                binding.homeTopUsernameTv.text = user.nickname
            })
        }

        //유저 정보 저장
        userInfo.gender = user.sex
        userInfo.height = user.height
        userInfo.weight = user.weight
        userInfo.walkNumber = user.walkNumber
    }

    /*날씨 조회 API*/
    override fun onWeatherSuccess(weather: Weather) {
        //UI 변경
        LogUtils.d("HOME(WEATHER)/API-SUCCESS", weather.toString())
        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {//visibility 조절
                binding.homeTopLineIv.visibility = View.VISIBLE
                binding.homeTopWeatherIv.visibility = View.VISIBLE
                binding.homeWeatherTempTv.visibility = View.VISIBLE
                binding.homeWeatherUnitTv.visibility = View.VISIBLE
                binding.homeWeatherConTv.visibility = View.VISIBLE

                binding.homeWeatherTempTv.text = weather.temperature
                binding.homeWeatherConTv.text = weather.weather
                val imgRes = when (weather.weather) {
                    "바람" -> R.drawable.ic_weather_windy
                    "맑음" -> R.drawable.ic_weather_sunny
                    "구름 많음" -> R.drawable.ic_weather_clounmany
                    "흐림" -> R.drawable.ic_weather_cloud
                    "비" -> R.drawable.ic_weather_rain
                    "비/눈" -> R.drawable.ic_weather_snoworrain
                    "눈" -> R.drawable.ic_weather_snowy
                    "소나기" -> R.drawable.ic_weather_shower
                    else -> R.drawable.ic_weather_sunny
                }
                binding.homeTopWeatherIv.setImageResource(imgRes)
            })
        }
    }

    /*일별 정보 조회 API*/
    override fun onTodaySuccess(today: Today) {
        userInfo.goalWalkTime = today.walkGoalTime
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

        //월별 API
        AchieveService.getTMonth(this)
    }

    /*월별 정보 조회 API*/
    override fun onTMonthSuccess(tMonth: TMonth) {
        LogUtils.d("HOME(TMONTH)/API-SUCCESS", tMonth.toString())

        if (view != null) {
            jobs.add(viewLifecycleOwner.lifecycleScope.launch {
                //누적 산책시간
                val monthTotalMin = tMonth.getMonthTotal.monthTotalMin
                val color = if (monthTotalMin > userInfo.walkNumber!!) "#FFC01D" else "#241F20"
                binding.homeMonthGoalWalkTv.setTextColor(Color.parseColor(color))
                binding.homeMonthGoalWalkTv.text = monthTotalMin.toString()
                binding.homeMonthGoalWalkTv.isSelected = true
                //누적 거리
                binding.homeMonthGoalDistTv.text =
                    tMonth.getMonthTotal.monthTotalDistance.toString()
                binding.homeMonthGoalDistTv.isSelected = true
                //누적 칼로리
                binding.homeMonthGoalKcalTv.text = tMonth.getMonthTotal.monthPerCal.toString()
                binding.homeMonthGoalKcalTv.isSelected = true
            })
        }

        // -> HomeMonthFragment
        (fragmentList[1] as HomeMonthFragment).onTMonthSuccess(tMonth)
    }

    /*API-FAIL*/
    override fun onHomeFailure(code: Int, message: String) {
        LogUtils.d("HOME/API-FAILURE", "code: $code message: $message")

        val text = if (!isNetworkAvailable(requireContext())) { //네트워크 에러
            getString(R.string.error_network)
        } else { //나머지
            getString(R.string.error_api_fail)
        }
        Snackbar.make(requireView(), text, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.action_retry)) {
                UserService.getUser(this)
                AchieveService.getToday(this)
                AchieveService.getTMonth(this)
                callWeatherAPI()
            }.show()
    }


    override fun onDestroyView() {
        //등록된 jobs cancel -> binding error 막기 위해
        for (job in jobs) {
            job.cancel()
        }
        super.onDestroyView()
    }
}