package com.footprint.footprint.ui.walk

import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.graphics.PointF
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentWalkmapBinding
import com.footprint.footprint.domain.model.CourseInfoModel
import com.footprint.footprint.domain.model.GetFootprintEntity
import com.footprint.footprint.domain.model.SaveWalkEntity
import com.footprint.footprint.domain.model.SaveWalkFootprintEntity
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.service.BackgroundWalkService
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.dialog.CourseInfoDialogFragment
import com.footprint.footprint.ui.dialog.FootprintDialogFragment
import com.footprint.footprint.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.*
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class WalkMapFragment : BaseFragment<FragmentWalkmapBinding>(FragmentWalkmapBinding::inflate),
    OnMapReadyCallback {
    private lateinit var map: NaverMap
    private lateinit var locationOverlay: LocationOverlay
    private lateinit var currentPathOverlay: PathOverlay
    private val startMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_start)
    private val midMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_middle_end)
    private val endMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_end)
    private lateinit var spannable: SpannableString

    private lateinit var footprintDialogFragment: FootprintDialogFragment

    private var isWalking: Boolean = false
    private var paths = mutableListOf<MutableList<LatLng>>()
    private var currentTime: Int = 0
    private var firstLocationCome = false
    private var isFootprint = false

    private val saveWalkFootprints: ArrayList<SaveWalkFootprintEntity> =
        arrayListOf() //지금까지 사용자가 기록한 총 데이터
    private val saveWalkEntity: SaveWalkEntity = SaveWalkEntity()  //산책 데이터
    private lateinit var userInfo: SimpleUserModel

    //산책 기록 임시저장 Job
    private val tempSaveWalkJob =
        CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
            while (true) {
                bindSaveWalkEntity()
                setTempWalk(Gson().toJson(saveWalkEntity))

                delay(600000)   //10분에 한번씩 임시 저장
            }
        }

    override fun initAfterBinding() {
        if (::map.isInitialized) {
            return
        }

        userInfo = (activity as WalkActivity).userInfo

        //산책 시작 시간 데이터 저장
        val current = LocalDateTime.now(TimeZone.getTimeZone("Asia/Seoul").toZoneId())
        saveWalkEntity.startAt = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        initBinding()
        initWalkMap()
        initFootprintDialog()   //FootprintDialogFragment 초기화
    }

    private fun initWalkMap() {
        val options = NaverMapOptions()
            .locationButtonEnabled(true)
            .compassEnabled(false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.walkmap_map_fragment) as MapFragment?
                ?: MapFragment.newInstance(options).also {
                    childFragmentManager.beginTransaction().add(R.id.walkmap_map_fragment, it)
                        .commit()
                }

        // 지도 비동기 호출
        mapFragment.getMapAsync(this)
    }

    // 지도 콜백
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap
        // 지도 설정
        setMap()

        // 백그라운드 서비스
        sendCommandToService(BackgroundWalkService.TRACKING_START_OR_RESUME)

        setObserver()

        (activity as WalkActivity).course?.let { course ->
            CoroutineScope(Dispatchers.Main).apply {
                initCourseOverlay(course, naverMap)
            }
        }
    }

    private fun setMap() {
        map.moveCamera(CameraUpdate.zoomTo(17.0))
        map.uiSettings.isZoomControlEnabled = false

        locationOverlay = map.locationOverlay
        locationOverlay.apply {
            icon = OverlayImage.fromResource(R.drawable.ic_location_overlay)
            anchor = PointF(0.5f, 0.5f)
            subIcon = null
        }
    }

    private fun setObserver() {
        BackgroundWalkService.isWalking.observe(viewLifecycleOwner, Observer { state ->
            isWalking = state
            // 산책 중이면
            if (isWalking) {
                binding.walkmapMiddleIv.isSelected = true
                // 발자국으로 인한 재시작이면
                if (isFootprint) {
                    isFootprint = false
                } else {
                    currentPathOverlay = getPath(requireContext())
                }
            } else { // 산책 중 아니면
                //LogUtils.d("$TAG/WALKMAP", "ISWALKING - false")
                binding.walkmapMiddleIv.isSelected = false
                locationOverlay.isVisible = false

                if (paths.isNotEmpty() && paths.last().isNotEmpty() && !isFootprint) {
                    if (paths.size == 1) {
                        getMarker(paths.last()[0], startMarkerImage).map = map
                    } else {
                        getMarker(paths.last()[0], midMarkerImage).map = map
                    }

                    getMarker(paths.last().last(), midMarkerImage).map = map
                }
            }
        })

        BackgroundWalkService.paths.observe(viewLifecycleOwner, Observer { paths ->
            this.paths = paths
            //LogUtils.d("$TAG/WALKMAP", paths.toString())

            if (paths.isNotEmpty() && paths.last().size >= 2) {
                currentPathOverlay.coords = paths.last()
                currentPathOverlay.map = map
            }
        })

        BackgroundWalkService.totalDistance.observe(viewLifecycleOwner, Observer { distance ->
            binding.walkmapDistanceNumberTv.text =
                String.format("%.1f", distance / 1000)
        })

        BackgroundWalkService.currentLocation.observe(viewLifecycleOwner, Observer { location ->
            if (location != null) {
                if (!firstLocationCome) {
                    firstLocationCome = true

                    afterFirstLocationBinding()
                }

                if (!locationOverlay.isVisible) {
                    locationOverlay.isVisible = true
                }

                updateLocation(location)
            }
        })

        BackgroundWalkService.currentTime.observe(viewLifecycleOwner, Observer { currentTime ->
            this.currentTime = currentTime

            updateTime(userInfo.goalWalkTime!! * 60)
        })

        BackgroundWalkService.pauseWalk.observe(viewLifecycleOwner, Observer { state ->
            if (state) {
                // 움직임 없어서 일시정지 됐을 때
                showStopWalkDialog()
            }
        })

        BackgroundWalkService.gpsStatus.observe(viewLifecycleOwner, Observer { state ->
            if (!state) {
                showSnackBar()
            }
        })
    }

    private fun afterFirstLocationBinding() {
        binding.walkLoadingPb.visibility = View.GONE

        binding.walkmapPlusIv.setOnClickListener {
            isFootprint = true
            setWalkState(false)

            if (saveWalkFootprints.size >= 9) {  //기록이 이미 9개가 됐으면
                //"발자국은 최대 9개까지 남길 수 있어요." 다이얼로그 화면 띄우기
                val action =
                    WalkMapFragmentDirections.actionGlobalMsgDialogFragment(getString(R.string.error_post_cnt_exceed))
                findNavController().navigate(action)

                // 산책 재시작
                sendCommandToService(BackgroundWalkService.TRACKING_RESUME_BY_FOOTPRINT)
            } else  //아직 9개가 안됐으면 -> 발자국 남기기 다이얼로그 화면 띄우기
                footprintDialogFragment.show(requireActivity().supportFragmentManager, null)
        }

        binding.walkmapMiddleIv.setOnClickListener {
            setWalkState(!isWalking)
        }

        binding.walkmapStopIv.setOnClickListener {
            showStopWalkDialog()    //실시간 기록을 중지할까요? 다이얼로그 화면 띄우기
        }

        tempSaveWalkJob.start() //산책 기록 임시 저장 Job START
    }

    private fun showSnackBar() {
        Snackbar.make(
            requireView(),
            getString(R.string.error_gps_none),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(getString(R.string.action_retry)) {
            val locationManager =
                requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSnackBar()
            } else {
                sendCommandToService(BackgroundWalkService.TRACKING_START_OR_RESUME)
            }
        }.show()
    }

    private fun initBinding() {
        binding.walkmapProgressBar.isEnabled = false

        (activity as WalkActivity).course?.let { course ->
            binding.walkmapCourseInfoTv.visibility = View.VISIBLE

            binding.walkmapCourseInfoTv.setOnClickListener {
                showCourseInfoDialog(course)
            }
        }
    }

    private fun setWalkState(isWalking: Boolean) {
        if (isWalking) {
            sendCommandToService(BackgroundWalkService.TRACKING_START_OR_RESUME)
        } else {
            sendCommandToService(BackgroundWalkService.TRACKING_PAUSE)
        }
    }

    private fun goToWalkAfterActivity() {
        bindSaveWalkEntity()

        val intent: Intent = Intent(requireActivity(), WalkAfterActivity::class.java)
        intent.putExtra("walk", Gson().toJson(saveWalkEntity))    //산책 정보 전달

        val walkActivity = (requireActivity() as WalkActivity)
        walkActivity.course?.let {
            intent.putExtra("course", Gson().toJson(it))
        }

        startActivity(intent)   //다음 화면(지금까지 기록된 산책, 기록 데이터 확인하는 화면)으로 이동
        walkActivity.finish()    //해당 액티비티 종료
    }

    private fun updateLocation(location: Location) {
        val position = LatLng(location)

        locationOverlay.apply {
            this.position = position
            bearing = location.bearing
        }

        map.moveCamera(CameraUpdate.scrollTo(position))

        updateCalorie()
        updatePace(location.speed)
    }

    private fun updateCalorie() {
        val calConstant = 0.0525 * userInfo.weight!! // 칼로리 상수 * 몸무게

        binding.walkmapCalorieNumberTv.text =
            (calConstant * (currentTime / 60)).roundToInt().toString()
    }

    private fun updatePace(speed: Float) {
        binding.walkmapPaceNumberTv.text = (speed * 90).roundToInt().toString()
    }

    private fun updateTime(goalTime: Int) {
        val maxProgress = 100

        if (currentTime < 3600) {
            spannable = SpannableString(
                String.format(
                    "%02d:%02d",
                    currentTime / 60,
                    currentTime % 60
                )
            )
        } else {
            spannable =
                SpannableString(
                    String.format(
                        "%02d:%02d:%02d",
                        currentTime / 3600,
                        currentTime % 3600 / 60,
                        currentTime % 3600 % 60
                    )
                )
        }

        setSpannableString()

        binding.walkmapProgressBar.progress =
            currentTime * maxProgress / goalTime
        binding.walkmapWalktimeNumberTv.text = spannable
    }

    private fun setSpannableString() {
        val spanColor = ForegroundColorSpan(getColor(requireContext(), R.color.secondary))

        if (currentTime < 60) {
            spannable.setSpan(
                spanColor,
                4,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            spannable.setSpan(spanColor, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun initCourseOverlay(
        course: CourseInfoModel,
        naverMap: NaverMap
    ) {
        course.coords.forEach {
            getPath(requireContext(), R.color.black_light).apply {
                coords = it
                map = naverMap
            }
        }
    }

    private fun showCourseInfoDialog(course: CourseInfoModel) {
        val courseJson = Gson().toJson(course)
        val action =
            WalkMapFragmentDirections.actionWalkMapFragmentToCourseInfoDialogFragment(courseJson)
        findNavController().navigate(action)
    }

    //실시간 기록을 중지할까요? 다이얼로그 화면 띄우기
    private fun showStopWalkDialog() {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", getString(R.string.msg_stop_realtime_record))
        bundle.putString("left", getString(R.string.action_cancel))
        bundle.putString("right", getString(R.string.action_stop))

        val actionDialogFragment: ActionDialogFragment = ActionDialogFragment()
        actionDialogFragment.arguments = bundle

        actionDialogFragment.show(requireActivity().supportFragmentManager, null)

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun leftAction(action: String) {
            }

            override fun rightAction(action: String) {
                if (view != null) {
                    lifecycleScope.launch {
                        setWalkState(false)
                        // 경로 끝 마크 추가
                        if (paths.isNotEmpty() && paths.last().isNotEmpty()) {
                            getMarker(paths.last().last(), endMarkerImage).map = map
                        }

                        goToWalkAfterActivity()
                    }
                }
            }
        })
    }

    // service
    private fun sendCommandToService(action: String) {
        val intent = Intent(context, BackgroundWalkService::class.java)
        intent.action = action

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }

    //산책 데이터 모델에 데이터 바인딩
    private fun bindSaveWalkEntity() {
        saveWalkEntity.walkTitle = "${userInfo.walkNumber!! + 1}번째 산책" //00번째 산책
        saveWalkEntity.walkTime = binding.walkmapWalktimeNumberTv.text.toString()    //산책 시간
        val current = LocalDateTime.now(TimeZone.getTimeZone("Asia/Seoul").toZoneId())
        saveWalkEntity.endAt =
            current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))   //산책 종료 시간
        saveWalkEntity.distance = binding.walkmapDistanceNumberTv.text.toString().toDouble() //산책 거리
        saveWalkEntity.coordinate = paths //산책 동선 좌표
        saveWalkEntity.calorie = binding.walkmapCalorieNumberTv.text.toString().toInt()  //칼로리
        saveWalkEntity.saveWalkFootprints = saveWalkFootprints    //발자국
    }

    private fun initFootprintDialog() {
        footprintDialogFragment = FootprintDialogFragment()

        footprintDialogFragment.setMyDialogCallback(object :
            FootprintDialogFragment.MyDialogCallback {
            override fun sendFootprint(saveWalkFootprint: SaveWalkFootprintEntity) {
                //"발자국을 남겼어요." 다이얼로그 화면 띄우기
                val action =
                    WalkMapFragmentDirections.actionGlobalMsgDialogFragment(getString(R.string.msg_leave_footprint))
                findNavController().navigate(action)

                sendCommandToService(BackgroundWalkService.TRACKING_RESUME_BY_FOOTPRINT) // 발자국 찍고 다시 시작할 때

                saveWalkFootprint.footprintImgIdx = saveWalkFootprints.size //발자국 아이콘 인덱스 번호 저장
                saveWalkFootprints.add(saveWalkFootprint)   //footprints 리스트에 발자국 추가

                // 발자국 마크 추가
                if (paths.isNotEmpty() && paths.last().isNotEmpty()) {
                    val lastLang = paths.last().last()
                    getFootPrintMarker(lastLang, saveWalkFootprints.size).map = map
                    saveWalkFootprint.coordinates = lastLang
                }
            }

            override fun sendUpdatedFootprint(saveWalkFootprint: SaveWalkFootprintEntity) {
            }

            override fun sendUpdatedFootprint(getFootprintEntity: GetFootprintEntity) {
            }

            //다이얼로그 프래그먼트에서 취소를 누르거나 뒤로 나왔을 때 -> 타이머 재생
            override fun cancel() {
                initFootprintDialog()   //FootprintDialogFragment 초기화

                if (!isWalking)
                    sendCommandToService(BackgroundWalkService.TRACKING_RESUME_BY_FOOTPRINT)    // 산책 재시작
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sendCommandToService(BackgroundWalkService.TRACKING_STOP)
        tempSaveWalkJob.cancel()    //산책 기록 임시 저장 종료
    }
}