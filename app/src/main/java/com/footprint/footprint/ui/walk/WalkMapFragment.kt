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
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentWalkmapBinding
import com.footprint.footprint.domain.model.SaveWalkFootprintEntity
import com.footprint.footprint.domain.model.SaveWalkEntity
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.service.Path
import com.footprint.footprint.service.BackgroundWalkService
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.dialog.FootprintDialogFragment
import com.footprint.footprint.utils.getAbsolutePathByBitmap
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.*
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class WalkMapFragment : BaseFragment<FragmentWalkmapBinding>(FragmentWalkmapBinding::inflate),
    OnMapReadyCallback {
    private lateinit var map: NaverMap
    private lateinit var locationOverlay: LocationOverlay
    private lateinit var currentPathOverlay: PathOverlay
    private val startMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_start)
    private val midMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_middle_end)
    private val endMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_end)

    private lateinit var footprintDialogFragment: FootprintDialogFragment
    private lateinit var spannable: SpannableString

    private var isWalking: Boolean = false
    private var paths = mutableListOf<Path>()
    private var currentTime: Int = 0
    private var firstLocationCome = false

    private var isFootprint = false

    private val saveWalkFootprints: ArrayList<SaveWalkFootprintEntity> = arrayListOf() //지금까지 사용자가 기록한 총 데이터
    private val saveWalkEntity: SaveWalkEntity = SaveWalkEntity()  //산책 데이터
    private lateinit var userInfo: SimpleUserModel

    override fun initAfterBinding() {
        if (::map.isInitialized) {
            return
        }

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

        userInfo = (activity as WalkActivity).userInfo!!
        saveWalkEntity.walkTitle = "${userInfo.walkNumber!!+1}번째 산책" //00번째 산책

        //산책 시작 시간 데이터 저장
        val current = LocalDateTime.now(TimeZone.getTimeZone("Asia/Seoul").toZoneId())
        saveWalkEntity.startAt = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        initFootprintDialog()   //FootprintDialogFragment 초기화
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
    }

    private fun setMap() {
        map.moveCamera(CameraUpdate.zoomTo(17.0))
        map.uiSettings.isZoomControlEnabled = false

        locationOverlay = map.locationOverlay
        locationOverlay.apply {
            icon = OverlayImage.fromResource(R.drawable.ic_location_overlay_png)
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
                    initPath()
                }
            } else { // 산책 중 아니면
                //LogUtils.d("$TAG/WALKMAP", "ISWALKING - false")
                binding.walkmapMiddleIv.isSelected = false
                locationOverlay.isVisible = false

                if (paths.isNotEmpty() && paths.last().isNotEmpty() && !isFootprint) {
                    if (paths.size == 1) {
                        putMarker(paths.last()[0], startMarkerImage)
                    } else {
                        putMarker(paths.last()[0], midMarkerImage)
                    }

                    putMarker(paths.last().last(), endMarkerImage)
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
                    setBinding()
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

    private fun setBinding() {
        binding.walkLoadingPb.visibility = View.GONE
        binding.walkmapProgressBar.isEnabled = false

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
    }

    private fun putMarker(locationPosition: LatLng, image: OverlayImage) {
        val marker = Marker()
        marker.position = locationPosition
        marker.anchor = PointF(0.5f, 0.5f)
        marker.icon = image
        marker.map = map
    }

    private fun putMarker(locationPosition: LatLng, footprintCount: Int) {
        val marker = Marker()
        marker.position = locationPosition
        marker.anchor = PointF(0.5f, 0.5f)
        marker.width = 100
        marker.height = 100
        marker.zIndex = 100

        marker.icon = when (footprintCount) {
            1 -> OverlayImage.fromResource(R.drawable.ic_foot_print1)
            2 -> OverlayImage.fromResource(R.drawable.ic_foot_print2)
            3 -> OverlayImage.fromResource(R.drawable.ic_foot_print3)
            4 -> OverlayImage.fromResource(R.drawable.ic_foot_print4)
            5 -> OverlayImage.fromResource(R.drawable.ic_foot_print5)
            6 -> OverlayImage.fromResource(R.drawable.ic_foot_print6)
            7 -> OverlayImage.fromResource(R.drawable.ic_foot_print7)
            8 -> OverlayImage.fromResource(R.drawable.ic_foot_print8)
            9 -> OverlayImage.fromResource(R.drawable.ic_foot_print9)
            else -> OverlayImage.fromResource(R.drawable.ic_foot_print9)
        }

        marker.map = map
    }

    private fun initPath() {
        currentPathOverlay = PathOverlay()
        currentPathOverlay.apply {
            width = 30
            color = getColor(requireContext(), R.color.primary)
            outlineWidth = 0
        }
    }

    private fun setWalkState(isWalking: Boolean) {
        if (isWalking) {
            sendCommandToService(BackgroundWalkService.TRACKING_START_OR_RESUME)
        } else {
            sendCommandToService(BackgroundWalkService.TRACKING_PAUSE)
        }
    }

    private fun containPaths() {
        if (paths.isNotEmpty()) {
            var latLngBounds = LatLngBounds.from(paths[0])
            if (paths.size > 1) {
                for (index in 1 until paths.size) {
                    latLngBounds = latLngBounds.union(LatLngBounds.from(paths[index]))
                }
            }

            val cameraUpdate = CameraUpdate.fitBounds(latLngBounds)
                .animate(CameraAnimation.Fly, 2000)
                .finishCallback {
                    takeSnapshotMap()
                }

            map.moveCamera(cameraUpdate)
        } else {
            takeSnapshotMap()
        }
    }

    private fun takeSnapshotMap() {
        map.takeSnapshot { bitmap ->    //산책 동선 사진
            saveWalkEntity.pathImg = getAbsolutePathByBitmap(requireContext(), bitmap)
            bindSaveWalkEntity()

            val intent: Intent = Intent(requireActivity(), WalkAfterActivity::class.java)
            intent.putExtra("walk", Gson().toJson(saveWalkEntity))    //산책 정보 전달
            startActivity(intent)   //다음 화면(지금까지 기록된 산책, 기록 데이터 확인하는 화면)으로 이동
            (requireActivity() as WalkActivity).finish()    //해당 액티비티 종료
        }
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

    //실시간 기록을 중지할까요? 다이얼로그 화면 띄우기
    private fun showStopWalkDialog() {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", getString(R.string.msg_stop_realtime_record))
        bundle.putString("action", getString(R.string.action_stop))

        val actionDialogFragment: ActionDialogFragment = ActionDialogFragment()
        actionDialogFragment.arguments = bundle

        actionDialogFragment.show(requireActivity().supportFragmentManager, null)

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun action1(isAction: Boolean) {
                if (isAction) {   // 사용자가 다이얼로그 화면에서 중지 버튼을 누른 경우
                    if (view != null) {
                        lifecycleScope.launch {
                            setWalkState(false)
                            //delay(1000)
                            containPaths() // 경로 모두 포함하도록 지도 카메라 이동
                        }
                    }
                }
            }

            override fun action2(isAction: Boolean) {
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
        saveWalkEntity.walkTime = binding.walkmapWalktimeNumberTv.text.toString()    //산책 시간
        val current = LocalDateTime.now(TimeZone.getTimeZone("Asia/Seoul").toZoneId())
        saveWalkEntity.endAt =
            current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))   //산책 종료 시간
        saveWalkEntity.distance = binding.walkmapDistanceNumberTv.text.toString().toDouble() //산책 거리
        saveWalkEntity.coordinate = getCoordinate() //산책 동선 좌표
        saveWalkEntity.calorie = binding.walkmapCalorieNumberTv.text.toString().toInt()  //칼로리
        saveWalkEntity.saveWalkFootprints = saveWalkFootprints    //발자국
    }

    // PathGroup -> List<List<Double>>
    private fun getCoordinate(): List<List<Double>> {
        val coordinate = arrayListOf<ArrayList<Double>>()

        paths.map {
            coordinate.add(arrayListOf())
            it.map { lang ->
                coordinate.last().add(lang.latitude)
                coordinate.last().add(lang.longitude)
            }
        }

        //LogUtils.d("$TAG/WALKMAP", coordinate.toString())
        return coordinate
    }

    private fun initFootprintDialog() {
        footprintDialogFragment = FootprintDialogFragment()

        footprintDialogFragment.setMyDialogCallback(object : FootprintDialogFragment.MyDialogCallback {
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
                    putMarker(lastLang, saveWalkFootprints.size)
                    saveWalkFootprint.coordinates = listOf(lastLang.latitude, lastLang.longitude)
                    //LogUtils.d("$TAG/WALKMAP", footprint.coordinate.toString())
                }
            }

            override fun sendUpdatedFootprint(saveWalkFootprint: SaveWalkFootprintEntity) {
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
    }
}