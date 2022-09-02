package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.view.Gravity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
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
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.dialog.CourseWarningDialogFragment
import com.footprint.footprint.ui.dialog.MsgDialogFragmentArgs
import com.footprint.footprint.ui.walk.WalkActivity
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.CourseDetailViewModel
import com.footprint.footprint.viewmodel.CourseViewModel
import com.google.android.gms.common.util.MapUtils
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseDetailActivity : BaseActivity<ActivityCourseDetailBinding>(ActivityCourseDetailBinding::inflate), OnMapReadyCallback {
    private val args: CourseDetailActivityArgs by navArgs()
    private lateinit var courseDTO: CourseDTO

    private lateinit var courseInfoModel: CourseInfoModel
    private val courseDetailVm: CourseDetailViewModel by viewModel()

    override fun initAfterBinding() {
        courseDTO = if(intent.hasExtra("course"))
            Gson().fromJson(intent.getStringExtra("course"), CourseDTO::class.java)
        else
            Gson().fromJson(args.course, CourseDTO::class.java)

        courseDetailVm.getCourseInfo(courseDTO.courseIdx.toInt())

        observe()
    }

    private fun setBinding() {
        binding.courseDetailBackIv.setOnClickListener {
            onBackPressed()
        }

        val tagRVAdapter = CourseTagRVAdapter(courseInfoModel.tags)
        binding.courseDetailTagRv.adapter = tagRVAdapter

        // 찜하기 버튼 관련
        binding.courseDetailLikeIv.isSelected = courseDTO.userCourseMark
        binding.courseDetailLikeIv.setOnClickListener {
           courseDetailVm.markCourse(courseDTO.courseIdx.toInt())
        }

        binding.courseDetailWalkStartBtn.setOnClickListener {
            val courseWarningDialogFragment = CourseWarningDialogFragment()

            courseWarningDialogFragment.setMyCallbackListener(object : CourseWarningDialogFragment.MyCallbackListener {
                override fun goBack() {
                    courseWarningDialogFragment.dismiss()
                }

                override fun walk() {
                    starWalkActivity()
                }
            })
            courseWarningDialogFragment.show(this.supportFragmentManager, null)
        }
    }

    private fun starWalkActivity() {
        val intent = Intent(this, WalkActivity::class.java)
        intent.putExtra("course", Gson().toJson(courseInfoModel))
        // 유저 정보도 필요
        val tmpUser = SimpleUserModel(weight = 0, height = 0, goalWalkTime = 30, walkNumber = 1)
        intent.putExtra("userInfo", Gson().toJson(tmpUser))

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
        naverMap.uiSettings.apply {
            isCompassEnabled = false
            isZoomControlEnabled = false
            isScaleBarEnabled = false
            logoGravity = Gravity.BOTTOM
            setLogoMargin(40, 0, 0, 80)
        }

        // 오버레이 생성
        CoroutineScope(Dispatchers.Main).launch {
            initOverlay(naverMap)
        }
    }

    private fun initOverlay(naverMap: NaverMap) {
        courseInfoModel.coords.forEach {
            getPath(this@CourseDetailActivity).apply {
                coords = it
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

        moveMapCameraWithPadding(courseInfoModel.coords as MutableList<MutableList<LatLng>>, naverMap, 100)
    }

    private fun observe(){
        courseDetailVm.courseInfo.observe(this, Observer {
            courseInfoModel = CourseMapper.mapperToCourseInfoModel(courseDTO, it)
            LogUtils.d("CourseDetail", courseInfoModel.toString())

            initCourseMap()
            bind()
            setBinding()
        })

        courseDetailVm.isMarked.observe(this, Observer{
            binding.courseDetailLikeIv.isSelected = it ?: false
        })
    }

    private fun bind(){
        binding.courseDetailCourseTitleTv.text = courseInfoModel.title
        binding.courseDetailDescriptionTv.text = courseInfoModel.description
        Glide.with(this)
            .load(courseInfoModel.previewImageUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(binding.courseDetailPreviewIv)

    }

}