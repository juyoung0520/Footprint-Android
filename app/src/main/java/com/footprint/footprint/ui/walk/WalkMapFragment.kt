package com.footprint.footprint.ui.main.home

import android.content.Intent
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentWalkmapBinding
import com.footprint.footprint.data.model.FootprintModel
import com.footprint.footprint.data.model.FootprintsModel
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import kotlin.math.roundToInt

class WalkMapFragment : BaseFragment<FragmentWalkmapBinding>(FragmentWalkmapBinding::inflate),
    OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var map: NaverMap

    private lateinit var path: PathOverlay
    private val pathArray: ArrayList<LatLng> = arrayListOf()

    private lateinit var walk: Walk

    private var isWalking: Boolean = true
    private var currentTime: Int = 0

    private var totalDistance: Float = 0.0f
    private lateinit var lastLocation: Location

    private var stopCount = 0

    private val footprints: FootprintsModel = FootprintsModel() //지금까지 사용자가 기록한 총 데이터

    override fun initAfterBinding() {
        setBinding()

        val options = NaverMapOptions()
            .locationButtonEnabled(true)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.walkmap_map_fragment) as MapFragment?
                ?: MapFragment.newInstance(options).also {
                    childFragmentManager.beginTransaction().add(R.id.walkmap_map_fragment, it)
                        .commit()
                }

        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)

        //실시간 글 작성하기 화면으로부터 전달 받는 post 데이터
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("post")
            ?.observe(viewLifecycleOwner) {
                Log.d("WalkMapFragment", "post observe -> $it")

                setWalkState(true)  //화면에서 다시 돌아오면 산책 시간을 다시 측정한다.

                if (it != null)
                    footprints.footprints.add(  //전역 변수인 posts 에 현재 기록한 post 데이터를 추가한다.
                        Gson().fromJson(
                            it,
                            FootprintModel::class.java
                        )
                    )
            }
    }

    private fun setBinding() {
        binding.walkmapProgressBar.isEnabled = false

        binding.walkmapPlusIv.setOnClickListener {
            setWalkState(false)

            if (pathArray.isNotEmpty()) {
                putMarker(pathArray.lastIndex, R.drawable.ic_pin_stroke)
            }

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
            stopWalk()
            showStopWalkDialog()    //실시간 기록을 중지할까요? 다이얼로그 화면 띄우기
        }
    }

    private fun putMarker(index: Int, resourceId: Int) {
        val marker = Marker()
        marker.position = pathArray[index]
        marker.icon = OverlayImage.fromResource(resourceId)
        marker.anchor = PointF(0.5f, 0.5f)
        marker.map = map
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                map.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap

        setMap()

        binding.walkLoadingPb.visibility = View.GONE

        walk = Walk(1800)
        walk.start()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    private fun setMap() {
        map.moveCamera(CameraUpdate.zoomTo(15.0))
        map.uiSettings.isZoomControlEnabled = false
        map.locationSource = locationSource
        map.locationTrackingMode = LocationTrackingMode.Follow

        val locationOverlay = map.locationOverlay
        locationOverlay.apply {
            icon = OverlayImage.fromResource(R.drawable.ic_location)
            circleColor = getColor(requireContext(), R.color.primary_62)
            circleRadius = 50
            anchor = PointF(0.5f, 0.5f)
            subIcon = null
        }

        path = PathOverlay()
        path.apply {
            width = 20
            color = getColor(requireContext(), R.color.primary_62)
            outlineWidth = 0
//            patternImage = OverlayImage.fromResource(R.drawable.bg_path_pattern)
//            patternInterval = 100
        }

        map.addOnOptionChangeListener {
            val mode = map.locationTrackingMode

            if (mode == LocationTrackingMode.NoFollow && isWalking) {
                map.locationTrackingMode = LocationTrackingMode.Follow
            }

            if (mode == LocationTrackingMode.Follow) {
                locationOverlay.subIcon = null
            }
        }

        map.addOnLocationChangeListener { location ->
            updateLocation(location)
        }
    }

    private fun updateLocation(location: Location) {
        if (location.speed == 0.0f) {
            stopCount++

            if (stopCount == 10) {
                stopWalk()
            }

            return
        }

        if (stopCount != 0) {
            stopCount = 0
        }

        pathArray.add(LatLng(location.latitude, location.longitude))

        if (pathArray.size >= 2) {
            updateDistance(location)

            updateCalorie()

            updatePace(location.speed)

            path.coords = pathArray
            path.map = map
        }

    }

    private fun stopWalk() {
        if (isWalking) {
            setWalkState(false)
        }

        if (::path.isInitialized && pathArray.size >= 2) {
            path.patternImage = null
            path.map = map
        }

        if (pathArray.size >= 2) {
            putMarker(0, R.drawable.ic_marker_start)
            putMarker(pathArray.lastIndex, R.drawable.ic_marker_end)
        }

        map.takeSnapshot { bitmap ->
        }
    }

    private fun setWalkState(isWalking: Boolean) {
        if (isWalking) {
            binding.walkmapMiddleIv.setImageResource(R.drawable.btn_pause)
            this.isWalking = true

            map.locationTrackingMode = LocationTrackingMode.Follow
        } else {
            binding.walkmapMiddleIv.setImageResource(R.drawable.btn_play)
            this.isWalking = false

            map.locationTrackingMode = LocationTrackingMode.None
        }
    }

    private fun updateDistance(location: Location) {
        if (!::lastLocation.isInitialized) {
            lastLocation = location
            return
        }

        totalDistance += location.distanceTo(lastLocation)
        binding.walkmapDistanceNumberTv.text =
            String.format("%.1f", totalDistance / 1000)

        lastLocation = location
    }

    private fun updateCalorie() {
        val calroieConstant = 0.0525 * 50

        binding.walkmapCalorieNumberTv.text =
            (calroieConstant * (currentTime / 60)).roundToInt().toString()
    }

    private fun updatePace(speed: Float) {
        binding.walkmapPaceNumberTv.text = (speed * 90).roundToInt().toString()
    }

    private inner class Walk(private val goalTime: Int) : Thread() {
        private val maxProgress = 100

        private lateinit var spannable: SpannableString
        private val spanColor = ForegroundColorSpan(getColor(requireContext(), R.color.secondary))

        override fun run() {
            super.run()
            try {
                while (true) {
                    if (isWalking) {
                        sleep(1000)
                        currentTime++

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

                        requireActivity().runOnUiThread {
                            binding.walkmapProgressBar.progress =
                                currentTime * maxProgress / goalTime
                            binding.walkmapWalktimeNumberTv.text = spannable
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("interrupt", "스레드가 종료되었습니다.")
            }

        }

        fun setSpannableString() {
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
    }

    //실시간 기록을 중지할까요? 다이얼로그 화면 띄우기
    private fun showStopWalkDialog() {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", getString(R.string.msg_stop_realtime_record))

        val actionDialogFragment: ActionDialogFragment = ActionDialogFragment()
        actionDialogFragment.arguments = bundle

        actionDialogFragment.show(requireActivity().supportFragmentManager, null)

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun finish(isFinished: Boolean) {
                if (isFinished) {   //사용자가 다이얼로그 화면에서 중지 버튼을 누른 경우
                    val intent: Intent = Intent(requireActivity(), WalkAfterActivity::class.java)

                    if (footprints.footprints.size != 0)
                        intent.putExtra(
                            "footprints",
                            Gson().toJson(footprints)
                        )  //우선 임의로 저장한 기록만 넘겨줌

                    startActivity(intent)   //다음 화면(지금까지 기록된 산책, 기록 데이터 확인하는 화면)으로 이동
                    (requireActivity() as WalkActivity).finish()    //해당 액티비티 종료
                } else    //사용자가 다이얼로그 화면에서 취소 버튼을 누른 경우
                    setWalkState(true)  //다시 타이머가 실행되도록
            }

            override fun save(isSaved: Boolean) {
            }

            override fun delete(isDelete: Boolean) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        walk.interrupt()
    }

}