package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.graphics.PointF
import android.location.Location
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.mapper.CourseMapper
import com.footprint.footprint.databinding.ActivityCourseDetailBinding
import com.footprint.footprint.domain.model.CourseInfoModel
import com.footprint.footprint.domain.model.SimpleUserModel
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.CourseTagRVAdapter
import com.footprint.footprint.ui.dialog.CourseWarningDialogFragment
import com.footprint.footprint.ui.walk.WalkActivity
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.getMarker
import com.footprint.footprint.utils.getPath
import com.footprint.footprint.utils.moveMapCameraWithPadding
import com.footprint.footprint.viewmodel.CourseDetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseDetailActivity :
    BaseActivity<ActivityCourseDetailBinding>(ActivityCourseDetailBinding::inflate),
    OnMapReadyCallback {
    private lateinit var networkErrSb: Snackbar

    private val args: CourseDetailActivityArgs by navArgs()
    private lateinit var courseDTO: CourseDTO

    private lateinit var courseInfoModel: CourseInfoModel
    private val courseDetailVm: CourseDetailViewModel by viewModel()

    private lateinit var locationSource: FusedLocationSource
    private var currentLocation: Location? = null
    private lateinit var startLocation: Location
    private lateinit var naverMap: NaverMap

    override fun initAfterBinding() {
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        courseDTO = if (intent.hasExtra("course"))
            Gson().fromJson(intent.getStringExtra("course"), CourseDTO::class.java)
        else
            Gson().fromJson(args.course, CourseDTO::class.java)

        bindCourseDTO()

        observe()

        courseDetailVm.getCourseInfo(courseDTO.courseIdx)

        binding.courseDetailBackIv.setOnClickListener {
            onBackPressed()
        }
    }

    private fun bindCourseDTO() {
        startLocation = Location("start").apply {
            latitude = courseDTO.startLat
            longitude = courseDTO.startLong
        }

        // 찜하기 버튼 관련
        binding.courseDetailLikeIv.isSelected = courseDTO.userCourseMark
        binding.courseDetailLikeIv.setOnClickListener {
            courseDetailVm.markCourse(courseDTO.courseIdx)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun observe() {
        courseDetailVm.mutableErrorType.observe(this, Observer {
            when (it) {
                ErrorType.NETWORK -> {
                    networkErrSb = Snackbar.make(
                        binding.root,
                        getString(R.string.error_network),
                        Snackbar.LENGTH_INDEFINITE
                    )

                    when (courseDetailVm.getErrorType()) {
                        "getCourseInfo" -> networkErrSb.setAction(getString(R.string.action_retry)) {
                            courseDetailVm.getCourseInfo(
                                courseDTO.courseIdx.toInt()
                            )
                        }
                        "markCourse" -> networkErrSb.setAction(getString(R.string.action_retry)) {
                            courseDetailVm.markCourse(
                                courseDTO.courseIdx.toInt()
                            )
                        }
                    }

                    networkErrSb.show()
                }
                else -> {
                    startErrorActivity("CourseSearchActivity")
                }
            }
        })

        courseDetailVm.courseInfo.observe(this, Observer {
            binding.courseDetailLoadingPb.visibility = View.GONE
            courseInfoModel = CourseMapper.mapperToCourseInfoModel(courseDTO, it)

            initCourseMap()
            bindCourseInfo()
        })

        courseDetailVm.isMarked.observe(this, Observer {
            binding.courseDetailLikeIv.isSelected = it ?: false
        })

        courseDetailVm.user.observe(this, Observer {
            starWalkActivity(it)
        })
    }

    private fun bindCourseInfo() {
        courseInfoModel.also {
            binding.courseDetailCourseTitleTv.text = it.title
            binding.courseDetailDescriptionTv.text = it.description
            binding.courseDetailDistanceTimeTv.text =
                String.format("%.2fkm 약 %d분", it.distance, it.time)
            binding.courseDetailParticipantCountTv.text = String.format("%d명", it.courseCount)
            binding.courseDetailLikeCountTv.text = String.format("%d개", it.courseLike)

            val tagRVAdapter = CourseTagRVAdapter(it.tags)
            binding.courseDetailTagRv.adapter = tagRVAdapter

            Glide.with(this)
                .load(it.previewImageUrl)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .into(binding.courseDetailPreviewIv)
        }

        binding.courseDetailWalkStartBtn.setOnClickListener {
            if (currentLocation == null) {
                binding.courseDetailLoadingPb.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (currentLocation!!.distanceTo(startLocation) >= 5000f) {
                showCourseWarningDialog()
            } else {
                courseDetailVm.getUser()
            }
        }

    }

    private fun showCourseWarningDialog() {
        val courseWarningDialogFragment = CourseWarningDialogFragment()

        courseWarningDialogFragment.setMyCallbackListener(object :
            CourseWarningDialogFragment.MyCallbackListener {
            override fun goBack() {
                courseWarningDialogFragment.dismiss()
            }

            override fun walk() {
                courseDetailVm.getUser()
            }
        })
        courseWarningDialogFragment.show(this.supportFragmentManager, null)
    }

    private fun starWalkActivity(userModel: SimpleUserModel) {
        val intent = Intent(this, WalkActivity::class.java)
        intent.putExtra("course", Gson().toJson(courseInfoModel))
        intent.putExtra("userInfo", Gson().toJson(userModel))

        startActivity(intent)
    }

    private fun initCourseMap() {
        val options = NaverMapOptions()
            .compassEnabled(false)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.course_detail_map_fragment) as MapFragment?
                ?: MapFragment.newInstance(options).also {
                    supportFragmentManager.beginTransaction().add(R.id.walk_detail_map_fragment, it)
                        .commit()
                }

        // 지도 비동기 호출
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap.apply {
            locationSource = this@CourseDetailActivity.locationSource
            locationTrackingMode = LocationTrackingMode.Follow
        }

        naverMap.addOnLocationChangeListener { location ->
            binding.courseDetailLoadingPb.visibility = View.GONE
            currentLocation = location
            // LogUtils.d("location", currentLocation.toString())
        }

        naverMap.uiSettings.apply {
            isLocationButtonEnabled = false
            isCompassEnabled = false
            isZoomControlEnabled = false
            isScaleBarEnabled = false
            logoGravity = Gravity.BOTTOM
            setLogoMargin(40, 0, 0, 80)
        }

        // 오버레이 생성
        lifecycleScope.launch {
            initOverlay()
        }
    }

    private fun initOverlay() {
        for (c in courseInfoModel.coords) {
            if (c.size < 2) continue

            getPath(this@CourseDetailActivity).apply {
                coords = c
                map = naverMap
            }
        }

        val startMarker = getMarker(
            courseInfoModel.coords.first().first(),
            OverlayImage.fromResource(R.drawable.ic_course_start),
            PointF(0.5f, 0.95f)
        )
        startMarker.map = naverMap

        val endMarker = getMarker(
            courseInfoModel.coords.last().last(),
            OverlayImage.fromResource(R.drawable.ic_course_end),
            PointF(0.5f, 0.95f)
        )
        endMarker.map = naverMap

        moveMapCameraWithPadding(
            courseInfoModel.coords as MutableList<MutableList<LatLng>>,
            naverMap,
            100
        )
    }

    override fun onStop() {
        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
        super.onStop()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 30000
    }
}