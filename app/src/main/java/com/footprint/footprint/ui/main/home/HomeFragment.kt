package com.footprint.footprint.ui.main.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.Weather
import com.footprint.footprint.domain.model.LocationModel
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.data.dto.TMonth
import com.footprint.footprint.data.dto.Today
import com.footprint.footprint.databinding.FragmentHomeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.HomeViewpagerAdapter
import com.footprint.footprint.ui.error.ErrorActivity
import com.footprint.footprint.ui.walk.WalkActivity
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.HomeViewModel
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate
import java.time.ZoneId

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate){
    private lateinit var networkErrSb: Snackbar
    private lateinit var getResult: ActivityResultLauncher<Intent>

    //뷰페이저, 프래그먼트
    private lateinit var homeVPAdapter: HomeViewpagerAdapter
    private lateinit var gpsDescDialog: AlertDialog
    private var backgroundGPSDialog: AlertDialog? = null
    private val fragmentList = arrayListOf<Fragment>(HomeDayFragment(), HomeMonthFragment())

    //뷰모델
    private val homeVm: HomeViewModel by sharedViewModel()
    private lateinit var user: SimpleUserModel
    private lateinit var weather: Weather
    private lateinit var today: Today
    private lateinit var tmonth: TMonth

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

    override fun initAfterBinding() {

        val gpsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getString(R.string.msg_foreground_gps)
        } else {
            getString(R.string.msg_gps)
        }

        gpsDescDialog = this.let {
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


        //Initialize
        initTB()
        initDate()

        setClickListener()
        observe()
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
            if (user.height != null && user.weight != null && user.walkNumber != null) {
                val intent = Intent(activity, WalkActivity::class.java)

                val userInfoJson = Gson().toJson(user)
                intent.putExtra("userInfo", userInfoJson)

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

    private fun initActivityResult(){
        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            when(result.resultCode){
                Activity.RESULT_CANCELED, ErrorActivity.BACK, ErrorActivity.CONTACT -> {

                    // 사용자 정보 조회 API에 문제 생긴 경우, 앱 종료
                    if(homeVm.getErrorType() == "getUser"){
                        if(activity != null)
                            requireActivity().finishAffinity()
                    }

                    // today, tmonth 정보 없으면 로딩바
                    if(!::today.isInitialized && !::tmonth.isInitialized)
                        showLoadingBar(true)
                }
            }
        }
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
                interval = 60 * 1000
            }
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    p0.let {
                        for (location in it.locations) {
                            LogUtils.d("WEATHER/API-READY", "location.latitude:  ${location.latitude.toInt()}, location.longitude: ${location.longitude.toInt()}")

                            //날씨 api 호출
                            val location = LocationModel(location.latitude.toInt().toString(), location.longitude.toInt().toString())
                            homeVm.getWeather(location)
                            locationClient.removeLocationUpdates(this)
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
        val gpsDeniedDialog: AlertDialog? = this.let {
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

    /*Observe & Bind*/
    private fun observe(){
        homeVm.mutableErrorType.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                ErrorType.NETWORK -> {
                    networkErrSb = Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE)

                    when(homeVm.getErrorType()){
                        "getUser" -> networkErrSb.setAction(getString(R.string.action_retry)) { homeVm.getUser() }
                        "getToday" -> networkErrSb.setAction(getString(R.string.action_retry)) { homeVm.getToday() }
                        "getTmonth" -> networkErrSb.setAction(getString(R.string.action_retry)) { homeVm.getUser() }
                        "getWeather" -> networkErrSb.setAction(getString(R.string.action_retry)) { callWeatherAPI() }
                    }
                    networkErrSb.show()
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    startErrorActivity(getResult, "HomeFragment")
                }
            }
        })

        homeVm.thisUser.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            this@HomeFragment.user = it
            user.walkNumber = user.walkNumber
            bind()
        })

        homeVm.thisWeather.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            this@HomeFragment.weather = it
            bind()
        })

        homeVm.thisToday.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            this@HomeFragment.today = it
            bind()

            showLoadingBar(false)
        })

        homeVm.thisTmonth.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            this@HomeFragment.tmonth = it
            bind()

            showLoadingBar(false)
        })
    }

    private fun bind(){
        if(::user.isInitialized){
            binding.homeTopUsernameTv.text = user.nickname

            if(::today.isInitialized){
                user.goalWalkTime = today.walkGoalTime

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
            }

            if(::tmonth.isInitialized ){
                //누적 산책시간
                val monthTotalMin = tmonth.getMonthTotal.monthTotalMin
                val color = if (monthTotalMin > user.walkNumber!!) "#FFC01D" else "#241F20"
                binding.homeMonthGoalWalkTv.setTextColor(Color.parseColor(color))
                binding.homeMonthGoalWalkTv.text = monthTotalMin.toString()
                binding.homeMonthGoalWalkTv.isSelected = true
                //누적 거리
                binding.homeMonthGoalDistTv.text =
                    tmonth.getMonthTotal.monthTotalDistance.toString()
                binding.homeMonthGoalDistTv.isSelected = true
                //누적 칼로리
                binding.homeMonthGoalKcalTv.text = tmonth.getMonthTotal.monthPerCal.toString()
                binding.homeMonthGoalKcalTv.isSelected = true
            }
        }

        if(::weather.isInitialized){
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
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivityResult()
    }
    override fun onResume() {
        super.onResume()
            callWeatherAPI()
            homeVm.getUser()
            homeVm.getToday()
            homeVm.getTmonth()
    }
    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }

    // 로딩바
    private fun showLoadingBar(show: Boolean){
        if(show){
            binding.homeLoadingBgV.visibility = View.VISIBLE
            binding.homeLoadingPb.visibility = View.VISIBLE
        }else{
            binding.homeLoadingBgV.visibility = View.GONE
            binding.homeLoadingPb.visibility = View.GONE
        }
    }
}