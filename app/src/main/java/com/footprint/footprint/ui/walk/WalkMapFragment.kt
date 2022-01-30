package com.footprint.footprint.ui.walk

import android.content.Intent
import android.graphics.PointF
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.model.FootprintModel
import com.footprint.footprint.data.model.FootprintsModel
import com.footprint.footprint.databinding.FragmentWalkmapBinding
import com.footprint.footprint.service.Path
import com.footprint.footprint.service.WalkService
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.*
import kotlin.math.roundToInt

class WalkMapFragment : BaseFragment<FragmentWalkmapBinding>(FragmentWalkmapBinding::inflate),
    OnMapReadyCallback {
    private lateinit var map: NaverMap
    private lateinit var locationOverlay: LocationOverlay
    private lateinit var currentPathOverlay: PathOverlay

    private var isWalking: Boolean = false
    private var paths = mutableListOf<Path>()
    private var currentTime: Int = 0
    private var isInit = false

    private lateinit var spannable: SpannableString

    private val footprints: FootprintsModel = FootprintsModel() //지금까지 사용자가 기록한 총 데이터

    override fun initAfterBinding() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val options = NaverMapOptions()
            .locationButtonEnabled(true)
            .compassEnabled(false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.walkmap_map_fragment) as MapFragment?
                ?: MapFragment.newInstance(options).also {
                    childFragmentManager.beginTransaction().add(R.id.walkmap_map_fragment, it)
                        .commit()
                }

        mapFragment.getMapAsync(this)

        //실시간 글 작성하기 화면으로부터 전달 받는 post 데이터
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("post")
            ?.observe(viewLifecycleOwner) {
                Log.d("WalkMapFragment", "post observe -> $it")

                setWalkState(true)  //화면에서 다시 돌아오면 산책 시간을 다시 측정한다.

                if (it != null) {
                    footprints.footprints.add(Gson().fromJson(it, FootprintModel::class.java))

                    if (paths.isNotEmpty() && paths.last().isNotEmpty()) {
                        putMarker(paths.last().last(), footprints.footprints.size)
                    }
                }
            }

        return binding.root
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap
        setMap()

        sendCommandToService(WalkService.TRACKING_START_OR_RESUME)

        setObserver()
    }

    private fun setMap() {
        Log.d("Walk/WalkMap", "setMap")
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
        Log.d("Walk/WalkMap", "setObserver")
        // argument 가져오기

        val startMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_start)
        val midMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_middle_end)
        val endMarkerImage = OverlayImage.fromResource(R.drawable.ic_marker_end)

        WalkService.isWalking.observe(viewLifecycleOwner, Observer { state ->
            Log.d("Walk/WalkMap", "setObserver/isWalking - ${state.toString()}")
            isWalking = state
            if (isWalking) {
                binding.walkmapMiddleIv.isSelected = true
                initPath()
            } else {
                binding.walkmapMiddleIv.isSelected = false
                locationOverlay.isVisible = false

                if (paths.isNotEmpty() && paths.last().isNotEmpty()) {
                    if (paths.size == 1) {
                        putMarker(paths.last()[0], startMarkerImage)
                    } else {
                        putMarker(paths.last()[0], midMarkerImage)
                    }

                    putMarker(paths.last().last(), endMarkerImage)
                }
            }
        })

        WalkService.paths.observe(viewLifecycleOwner, Observer { paths ->
            Log.d("Walk/WalkMap", "setObserver/paths - ${paths.toString()}")
            this.paths = paths

            if (paths.isNotEmpty() && paths.last().size >= 2) {
                currentPathOverlay.coords = paths.last()
                currentPathOverlay.map = map
            }
        })

        WalkService.totalDistance.observe(viewLifecycleOwner, Observer { distance ->
            Log.d("Walk/WalkMap", "setObserver/totalDistance - ${distance.toString()}")
            binding.walkmapDistanceNumberTv.text =
                String.format("%.1f", distance / 1000)
        })

        WalkService.currentLocation.observe(viewLifecycleOwner, Observer { location ->
            if (location != null) {
                if (!isInit) {
                    isInit = true
                    setBinding()
                }

                Log.d("Walk/WalkMap", "setObserver/currentLocation - ${location.toString()}")
                if (!locationOverlay.isVisible) {
                    locationOverlay.isVisible = true
                }

                updateLocation(location)
            } else {
                Log.d("Walk/WalkMap", "setObserver/currentLocation - null")
            }
        })

        WalkService.currentTime.observe(viewLifecycleOwner, Observer { currentTime ->
            this.currentTime = currentTime

            updateTime(1800)
        })

        WalkService.pauseWalk.observe(viewLifecycleOwner, Observer { state ->
            if (state) {
                showStopWalkDialog()
            }
        })
    }

    private fun setBinding() {
        Log.d("Walk/WalkMap", "setBinding")
        binding.walkLoadingPb.visibility = View.GONE
        binding.walkmapProgressBar.isEnabled = false

        binding.walkmapPlusIv.setOnClickListener {
            setWalkState(false)

            if (footprints.footprints.size >= 9) {  //기록이 이미 9개가 됐으면
                //"발자국은 최대 9개까지 남길 수 있어요." 다이얼로그 화면 띄우기
                val action =
                    WalkMapFragmentDirections.actionGlobalMsgDialogFragment(getString(R.string.error_post_cnt_exceed))
                findNavController().navigate(action)
            } else {    //아직 9개가 안됐으면 -> 발자국 남기기 다이얼로그 화면 띄우기
                val action =
                    WalkMapFragmentDirections.actionWalkMapFragmentToFootprintDialogFragment()
                findNavController().navigate(action)
            }
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
            sendCommandToService(WalkService.TRACKING_START_OR_RESUME)
        } else {
            sendCommandToService(WalkService.TRACKING_PAUSE)
        }
    }

    private fun stopWalk() {
        if (paths.isNotEmpty()) {
            var latLngBounds = LatLngBounds.from(paths[0])
            if (paths.size > 1) {
                for (index in 1 until paths.size) {
                    latLngBounds = latLngBounds.union(LatLngBounds.from(paths[index]))
                }
            }
            map.moveCamera(CameraUpdate.fitBounds(latLngBounds))
        }

        map.takeSnapshot { bitmap ->
        }
    }

    private fun updateLocation(location: Location) {
        val position = LatLng(location)

        locationOverlay.apply {
            this.position = position
            bearing = location.bearing
        }

        map.moveCamera(CameraUpdate.scrollTo(position))

        updateCalorie(50)
        updatePace(location.speed)
    }

    private fun updateCalorie(weight: Int) {
        val calConstant = 0.0525 * weight // 칼로리 상수 * 몸무게

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

        val actionDialogFragment: ActionDialogFragment = ActionDialogFragment()
        actionDialogFragment.arguments = bundle

        actionDialogFragment.show(requireActivity().supportFragmentManager, null)

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {

            override fun action1(isAction: Boolean) {
                if (isAction) {   //사용자가 다이얼로그 화면에서 중지 버튼을 누른 경우
                    val intent: Intent = Intent(requireActivity(), WalkAfterActivity::class.java)

                    if (footprints.footprints.size != 0)
                        intent.putExtra(
                            "footprints",
                            Gson().toJson(footprints)
                        )  //우선 임의로 저장한 기록만 넘겨줌

                    startActivity(intent)   //다음 화면(지금까지 기록된 산책, 기록 데이터 확인하는 화면)으로 이동
                    (requireActivity() as WalkActivity).finish()    //해당 액티비티 종료
                } else { //사용자가 다이얼로그 화면에서 취소 버튼을 누른 경우
//                    setWalkState(true)  //다시 타이머가 실행되도록
                }
            }

            override fun action2(isAction: Boolean) {
            }
        })
    }

    // service
    private fun sendCommandToService(action: String) {
        Log.d("Walk/WalkMap", "sendCommandToService")
        val intent = Intent(context, WalkService::class.java)
        intent.action = action

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sendCommandToService(WalkService.TRACKING_STOP)
    }

}